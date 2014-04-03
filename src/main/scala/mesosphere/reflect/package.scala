package mesosphere

package object reflect {

  protected[mesosphere] lazy val classLoaderMirror =
    scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)

}
