package mesosphere.reflect

import scala.reflect.runtime.universe._

/**
  * Encapsulates functionality to reflectively invoke the constructor
  * for a given case class type.
  */
protected[mesosphere] class CaseClassFactory(cls: Class[_]) {

  val classSymbol = classLoaderMirror.classSymbol(cls)
  val tpe = classSymbol.toType

  if (!(tpe <:< typeOf[Product] && classSymbol.isCaseClass))
    throw new IllegalArgumentException(
      "CaseClassFactory only applies to case classes!"
    )

  val classMirror = classLoaderMirror reflectClass classSymbol

  val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR)

  val defaultConstructor =
    if (constructorSymbol.isMethod) constructorSymbol.asMethod
    else {
      val ctors = constructorSymbol.asTerm.alternatives
      ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
    }

  val constructorMethod = classMirror reflectConstructor defaultConstructor

  /**
    * Attempts to create a new instance of the specified type by calling the
    * constructor method with the supplied arguments.
    *
    * @param args the arguments to supply to the constructor method
    */
  def buildWith(args: Seq[_]): Any = constructorMethod(args: _*)

}
