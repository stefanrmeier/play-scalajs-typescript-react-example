package utils.es

import org.specs2.mutable.Specification
import play.api.Mode
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * Created by stefanrmeier 2017/05/30.
 */
class Elastic4sUtilsTest extends Specification {

  val injector: Injector = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .injector

  val elastic4sUtils = injector.instanceOf[Elastic4sUtils]

  "drop" in {
    elastic4sUtils.dropSchema
    1 mustEqual (1)
  }

  "createSchema" in {
    elastic4sUtils.createSchema
    1 mustEqual (1)
  }

}
