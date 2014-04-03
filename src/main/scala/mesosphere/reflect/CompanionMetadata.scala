package mesosphere.reflect

import scala.reflect.runtime.universe._

/**
  * Wraps information about a companion object for a type.
  */
protected[mesosphere] case class CompanionMetadata(
  symbol: ModuleSymbol,
  instance: Any,
  instanceMirror: InstanceMirror,
  classType: Type)

protected[mesosphere] object CompanionMetadata {

  /**
    * Returns a Some wrapping CompanionMetadata for the supplied class type, if
    * that class type has a companion, and None otherwise.
    */
  def apply(classSymbol: ClassSymbol): Option[CompanionMetadata] = {

    // None if the supplied class type has no companion
    val companionSymbol: Option[ModuleSymbol] =
      if (!classSymbol.companionSymbol.isModule) None
      else Some(classSymbol.companionSymbol.asModule)

    companionSymbol.map { symbol =>
      val instance = classLoaderMirror.reflectModule(symbol).instance
      val instanceMirror = classLoaderMirror reflect instance
      val classType = symbol.moduleClass.asClass.asType.toType
      CompanionMetadata(symbol, instance, instanceMirror, classType)
    }
  }
}
