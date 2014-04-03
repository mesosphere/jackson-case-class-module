package mesosphere.jackson

import com.fasterxml.jackson.module.scala.DefaultScalaModule

object CaseClassModuleSpec {
  case class Person(name: String, age: Int)
  case class Defaults(x: Double = math.E, y: Double = math.Pi, z: String = "foobar")
  case class ComplexDefaults(xs: Seq[Int] = Seq(1, 2, 3))
  case class NestedDefaults(defaults: ComplexDefaults = ComplexDefaults(Seq(5)))
}

class CaseClassModuleSpec extends Spec with JacksonHelpers {

  import CaseClassModuleSpec._

  val module = new DefaultScalaModule with CaseClassModule

  "CaseClassModule" should "deserialize basic case classes" in {
    deserialize[Person]("""{"name": "Jaime", "age": 17}""") should equal (Person("Jaime", 17))
  }

  it should "respect default values for basic case classes" in {
    deserialize[Defaults]("{}") should equal (Defaults(math.E, math.Pi, "foobar"))
  }

  it should "respect default values for complex case classes" in {
    deserialize[ComplexDefaults]("{}") should equal (ComplexDefaults(Seq(1, 2, 3)))
  }

  it should "respect default values for nested case classes" in {
    deserialize[NestedDefaults]("{}") should equal (NestedDefaults(ComplexDefaults(Seq(5))))
    deserialize[NestedDefaults]("""{"defaults": {} }""") should equal (NestedDefaults(ComplexDefaults(Seq(1, 2, 3))))
  }

}
