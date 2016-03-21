package mesosphere.jackson

import mesosphere.reflect.{ CaseClassFactory, CompanionMetadata }

import com.fasterxml.jackson.databind.{
  BeanDescription,
  DeserializationConfig,
  DeserializationContext,
  JavaType,
  PropertyMetadata,
  PropertyName
}
import com.fasterxml.jackson.databind.deser.CreatorProperty
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator
import com.fasterxml.jackson.databind.`type`.{ TypeBindings, TypeFactory }

import scala.collection.immutable.ListMap
import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._
import scala.util.Try

protected class CaseClassValueInstantiator(
  config: DeserializationConfig,
  beanDesc: BeanDescription)
    extends StdValueInstantiator(config, beanDesc.getType) {

  import mesosphere.reflect.classLoaderMirror

  val classSymbol = classLoaderMirror.classSymbol(beanDesc.getBeanClass)

  assert(
    classSymbol.isCaseClass,
    "%s: refusing to instantiate non-case-class type [%s]".format(
      getClass.getName,
      beanDesc.getBeanClass.getName
    )
  )

  /**
    * Contains `Some(value)` if there is a default value for the associated
    * parameter name for the supplied case class type and `None` otherwise.
    */
  private[this] lazy val defaultArguments: ListMap[String, Option[() => Any]] = {
    val companion = CompanionMetadata(classSymbol).get

    val applySymbol: MethodSymbol = {
      val symbol = companion.classType.member("apply": TermName)
      if (symbol.isMethod) symbol.asMethod
      else symbol.asTerm.alternatives.head.asMethod // symbol.isTerm
    }

    def valueFor(i: Int): Option[() => Any] = {
      val defaultThunkName = s"apply$$default$$${i + 1}": TermName
      val defaultThunkSymbol = companion.classType member defaultThunkName

      if (defaultThunkSymbol == NoSymbol) None
      else {
        val defaultThunk =
          companion.instanceMirror reflectMethod defaultThunkSymbol.asMethod
        Some(() => defaultThunk.apply())
      }
    }

    ListMap(applySymbol.paramss.flatten.zipWithIndex.map {
      case (p, i) =>
        p.name.toString -> valueFor(i)
    }: _*)
  }

  private[this] lazy val factory =
    new CaseClassFactory(beanDesc.getBeanClass)

  private[this] lazy val typeBindings =
    new TypeBindings(config.getTypeFactory, beanDesc.getType)

  private[this] lazy val ctorProps = for {
    prop <- beanDesc.findProperties().asScala
    param <- Option(prop.getConstructorParameter)
    name = new PropertyName(prop.getName)
    wrapperName = prop.getWrapperName
    idx = param.getIndex
    javaType = param.getType(typeBindings)
  } yield {
    new CreatorProperty(
      name,
      javaType,
      wrapperName,
      null,
      null,
      param,
      idx,
      null,
      PropertyMetadata.STD_OPTIONAL
    )
  }

  val creator = beanDesc.getConstructors.asScala.headOption

  // configure this value instantiator to be used only via createFromObjectWith
  configureFromObjectSettings(
    null, null, null, null,
    creator.orNull,
    creator.map(_ => ctorProps.toArray).orNull
  )

  override def createFromObjectWith(
    cxt: DeserializationContext,
    args: Array[Object]): Object = {
    val params: Seq[_] = (args.toSeq zip defaultArguments.values).map {
      case (None, Some(default)) => default.apply
      case (null, Some(default)) => default.apply
      case (deserialized, _)     => deserialized
    }
    factory.buildWith(params.toArray.toSeq).asInstanceOf[Object]
  }

}
