package controllers.test

import org.specs2.mutable.Specification
import play.api.libs.json.{ JsValue, Json }

import scala.pickling.json.JSONPickle
import scala.pickling.Defaults._, scala.pickling.json._

/**
 * Created by stefanrmeier 2017/04/21.
 */
class JSendTest extends Specification {

  "test serialisation of null" in {
    case class WithNull(status: String, data: String = null)
    case class WithOption(status: String, data: Option[String] = None)

    val withNull = WithNull("success")
    val withOption = WithOption("success")

    import scala.pickling.Defaults._, scala.pickling.json._
    val withNullPickle = withNull.pickle
    val withOptionPickle = withOption.pickle

    println("WithNull: " + withNullPickle)
    println("WithOption: " + withOptionPickle)

    val withNullPickleValue = withNull.pickle.value
    val withOptionPickleValue = withOption.pickle.value
    println("WithNull: " + withNullPickleValue)
    println("WithOption: " + withOptionPickleValue)

    val withNullPickleValueJson = Json.parse(withNullPickleValue)
    val withOptionPickleValueJson = Json.parse(withOptionPickleValue)
    println("withNullPickleValueJson: " + withNullPickleValueJson)
    println("withOptionPickleValueJson: " + withOptionPickleValueJson)
    1 shouldEqual 1
  }

  "test String serialization" in {

    1 shouldEqual 1
  }

}

//trait Payload
//
//sealed trait JSendStatus
//object JSendStatus {
//
//  case object Success extends JSendStatus {
//    override def toString: String = "success"
//  }
//
//  case object Fail extends JSendStatus {
//    override def toString: String = "fail"
//  }
//
//  case object Error extends JSendStatus {
//    override def toString: String = "error"
//  }
//
//}
//
//sealed trait JSend{
//  val status: JSendStatus
//}
//case class JSendSuccess(data: Payload = null) extends JSend {
//  override val status = JSendStatus.Success
//}
//case class JSendFail(data: Payload = null) extends JSend {
//  override val status = JSendStatus.Fail
//}
//case class JSendError(message: String, data: Option[Payload] = None, code: Option[String]=None ) extends JSend {
//  override val status = JSendStatus.Error
//}

