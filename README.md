# Jackson Case Class Module

Provides deserialization support for Scala case classes, including proper handling of default values.

Assumes that the default case class constructor's arguments are sufficient for deserialization. 

## Usage

```scala
import mesosphere.jackson.CaseClassModule

val mapper = new ObjectMapper
mapper.registerModule(CaseClassModule)

case class Person(name: String, age: Int = 30)

val readResult = mapper.readValue(
  """{ "name": "Alfonso" }""",
  classOf[Person]
)
```
