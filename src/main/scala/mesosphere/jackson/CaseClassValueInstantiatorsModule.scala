package mesosphere.jackson

import com.fasterxml.jackson.module.scala.JacksonModule

trait CaseClassValueInstantiatorsModule extends JacksonModule {
  this += { _.addValueInstantiators(CaseClassValueInstantiators) }
}
