# Jackson Case Class Module

Provides deserialization support for Scala case classes, including proper handling of default values.

Assumes that the default case class constructor's arguments are sufficient for deserialization. 

**SBT:**

```scala
resolvers += "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven"
libraryDependencies += "mesosphere" %% "jackson-case-class-module" % "0.1.0"
```

**Maven:**

```xml
<repository>
  <id>mesosphere-public-repo</id>
  <name>Mesosphere Public Repo</name>
  <url>http://downloads.mesosphere.io/maven</url>
</repository>

<dependency>
  <groupId>mesosphere</groupId>
  <artifactId>jackson-case-class-module_2.10</artifactId>
  <version>0.1.0</version>
</dependency>
```

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
