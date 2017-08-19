package utils

import scala.scalajs.js.Date
import scala.scalajs.js.annotation.{ JSExport, JSExportTopLevel }
import scala.util.Try

@JSExportTopLevel("Utils.Validator")
object Validotor {

  @JSExport
  def isValideEmail(s: String): Boolean = {
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    s match {
      case s if !minLength(s, 1) => false
      case s if emailRegex.findFirstMatchIn(s).isDefined => true
      case _ => false
    }
  }

  @JSExport
  def isValidPhone(s: String): Boolean = {

    /*
     See https://stackoverflow.com/questions/16699007/regular-expression-to-match-standard-10-digit-phone-number
     */
    val phoneRegex =
      """^(\+(\d{1,3}))(\d{10})(?: *x(\d+))?\s*$""".r

    def sanitizedString(s: String) = s.replace("(0)", "").replace(" ", "").replace("(", "").replace(")", "").replace("-", "").replace("/", "")

    s match {
      case s if !minLength(s, 1) => false
      case s if phoneRegex.findFirstMatchIn(sanitizedString(s)).isDefined => true
      case _ => false
    }
  }

  @JSExport
  def isValidDate(s: String): Boolean = {
    def extractDate(s: String): Boolean = {
      Try {
        val d: Date = new Date(Date.parse(s))
        val t = d.toISOString()
        t
      }.isSuccess
    }
    s match {
      case s if !minLength(s, 1) => false
      case s if extractDate(s) => true
      case _ => false
    }
  }

  @JSExport
  def maxLength(s: String, length: Int): Boolean = s match {
    case null => false
    case s if s.trim.isEmpty => false
    case s if s.size <= length => true
    case _ => false
  }

  @JSExport
  def minLength(s: String, length: Int): Boolean = s match {
    case null => false
    case s if s.trim.isEmpty => false
    case s if s.size >= length => true
    case _ => false
  }
}