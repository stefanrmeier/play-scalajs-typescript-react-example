package controllers

import java.net.URLEncoder
import java.util.UUID

import com.example.system.modules.BasicComponents
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{ SecuredRequest, UserAwareRequest }
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import utils.auth.DefaultEnv
import utils.tracking.{ MarketingCookie, TrackingEvent }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.pickling.{ Pickler, Unpickler }
import scala.language.higherKinds
import scala.pickling.json.JSONPickle

/**
 * Created by stefan.meier on 23-02-2017.
 */

class RequestWrapper[A, R <: Request[A]](request: R) extends WrappedRequest[A](request)

class TrackedRequest[A, R <: Request[A]](val visitor: String, val tknCookies: List[Cookie], request: R) extends RequestWrapper[A, R](request)

trait BasicController extends Controller {
  implicit val basicComponents: BasicComponents
  val silhouette: Silhouette[DefaultEnv] = basicComponents.silhouette

  case class STrackedRequest[A](override val visitor: String, override val tknCookies: List[Cookie], request: SecuredRequest[DefaultEnv, A]) extends TrackedRequest[A, SecuredRequest[DefaultEnv, A]](visitor, tknCookies, request)
  case class UATrackedRequest[A](override val visitor: String, override val tknCookies: List[Cookie], request: UserAwareRequest[DefaultEnv, A]) extends TrackedRequest[A, UserAwareRequest[DefaultEnv, A]](visitor, tknCookies, request)
  case class UTrackedRequest[A](override val visitor: String, override val tknCookies: List[Cookie], request: Request[A]) extends TrackedRequest[A, Request[A]](visitor, tknCookies, request)

  //TODO duplicated code has to be refactored
  object STAction extends ActionBuilder[STrackedRequest] {
    val cookieValidityTime = 3600 * 24 * 7 //7 days
    val cookiePath = "/"
    override def invokeBlock[A](request: Request[A], block: (STrackedRequest[A]) => Future[Result]): Future[Result] = {
      val visitor = request.session.get(TrackingEvent.VISITOR_KEY).getOrElse(UUID.randomUUID().toString)
      val tknCookie = request.cookies.get(TrackingEvent.TRACKING_COOKIE_KEY).getOrElse(Cookie(TrackingEvent.TRACKING_COOKIE_KEY, visitor, Some(cookieValidityTime), cookiePath))
      val mktCookie = request.cookies.get(TrackingEvent.MARKETING_COOKIE_KEY).
        orElse(
          for {
            utm_source <- request.getQueryString("utm_source") if !utm_source.isEmpty
            utm_medium <- request.getQueryString("utm_medium") if !utm_medium.isEmpty
          } yield {
            val utm_campaign = request.getQueryString("utm_campaign")
            val mc = MarketingCookie(Option(utm_source), Option(utm_medium), utm_campaign)
            import play.api.libs.json._
            val mcJson: JsValue = Json.toJson(mc)
            Cookie(TrackingEvent.MARKETING_COOKIE_KEY, URLEncoder.encode(Json.stringify(mcJson), "utf-8"), Some(cookieValidityTime), cookiePath)
          }
        )

      val trackedRequest = STrackedRequest(visitor, List(tknCookie) ::: mktCookie.toList, request.asInstanceOf[SecuredRequest[DefaultEnv, A]])

      val responsef: Future[Result] = block(trackedRequest)
      responsef.map(res => res.
        withCookies(trackedRequest.tknCookies: _*).
        withSession(res.session(request) - TrackingEvent.VISITOR_KEY + (TrackingEvent.VISITOR_KEY -> trackedRequest.visitor))
      )
    }
  }

  object UATAction extends ActionBuilder[UATrackedRequest] {
    val cookieValidityTime = 3600 * 24 * 7 //7 days
    val cookiePath = "/"
    override def invokeBlock[A](request: Request[A], block: (UATrackedRequest[A]) => Future[Result]): Future[Result] = {
      val visitor = request.session.get(TrackingEvent.VISITOR_KEY).getOrElse(UUID.randomUUID().toString)
      val tknCookie = request.cookies.get(TrackingEvent.TRACKING_COOKIE_KEY).getOrElse(Cookie(TrackingEvent.TRACKING_COOKIE_KEY, visitor, Some(cookieValidityTime), cookiePath))
      val mktCookie = request.cookies.get(TrackingEvent.MARKETING_COOKIE_KEY).
        orElse(
          for {
            utm_source <- request.getQueryString("utm_source") if !utm_source.isEmpty
            utm_medium <- request.getQueryString("utm_medium") if !utm_medium.isEmpty
          } yield {
            val utm_campaign = request.getQueryString("utm_campaign")
            val mc = MarketingCookie(Option(utm_source), Option(utm_medium), utm_campaign)
            import play.api.libs.json._
            val mcJson: JsValue = Json.toJson(mc)
            Cookie(TrackingEvent.MARKETING_COOKIE_KEY, URLEncoder.encode(Json.stringify(mcJson), "utf-8"), Some(cookieValidityTime), cookiePath)
          }
        )

      val trackedRequest = UATrackedRequest(visitor, List(tknCookie) ::: mktCookie.toList, request.asInstanceOf[UserAwareRequest[DefaultEnv, A]])

      val responsef: Future[Result] = block(trackedRequest)
      responsef.map(res => res.
        withCookies(trackedRequest.tknCookies: _*).
        withSession(res.session(request) - TrackingEvent.VISITOR_KEY + (TrackingEvent.VISITOR_KEY -> trackedRequest.visitor))
      )
    }
  }

  object UTAction extends ActionBuilder[UTrackedRequest] {
    val cookieValidityTime = 3600 * 24 * 7 //7 days
    val cookiePath = "/"
    override def invokeBlock[A](request: Request[A], block: (UTrackedRequest[A]) => Future[Result]): Future[Result] = {
      val visitor = request.session.get(TrackingEvent.VISITOR_KEY).getOrElse(UUID.randomUUID().toString)
      val tknCookie = request.cookies.get(TrackingEvent.TRACKING_COOKIE_KEY).getOrElse(Cookie(TrackingEvent.TRACKING_COOKIE_KEY, visitor, Some(cookieValidityTime), cookiePath))
      val mktCookie = request.cookies.get(TrackingEvent.MARKETING_COOKIE_KEY).
        orElse(
          for {
            utm_source <- request.getQueryString("utm_source") if !utm_source.isEmpty
            utm_medium <- request.getQueryString("utm_medium") if !utm_medium.isEmpty
          } yield {
            val utm_campaign = request.getQueryString("utm_campaign")
            val mc = MarketingCookie(Option(utm_source), Option(utm_medium), utm_campaign)
            import play.api.libs.json._
            val mcJson: JsValue = Json.toJson(mc)
            Cookie(TrackingEvent.MARKETING_COOKIE_KEY, URLEncoder.encode(Json.stringify(mcJson), "utf-8"), Some(cookieValidityTime), cookiePath)
          }
        )

      val trackedRequest = UTrackedRequest(visitor, List(tknCookie) ::: mktCookie.toList, request)

      val responsef: Future[Result] = block(trackedRequest)
      responsef.map(res => res.
        withCookies(trackedRequest.tknCookies: _*).
        withSession(res.session(request) - TrackingEvent.VISITOR_KEY + (TrackingEvent.VISITOR_KEY -> trackedRequest.visitor))
      )
    }
  }

  def TrackedSecureAction: ActionBuilder[STrackedRequest] = silhouette.SecuredAction andThen STAction

  def TrackedUserAwareAction: ActionBuilder[UATrackedRequest] = silhouette.UserAwareAction andThen UATAction

  def Action: ActionBuilder[UTrackedRequest] = silhouette.UnsecuredAction andThen UTAction

}

case class Catalog(id: String, text: String, active: Boolean, parentId: Option[String] = None) extends Payload

trait Payload {
  def toPickle: JSONPickle = Payload.toPickle(this)
  def serialize: String = toPickle.value
  def toJson: JsValue = Json.parse(serialize)
}

object Payload {
  import scala.pickling.Defaults._, scala.pickling.json._
  import scala.pickling.{ FastTypeTag, PBuilder, PReader, PicklingException }
  type PrimitivePicklers = scala.pickling.pickler.PrimitivePicklers
  type PrimitiveArrayPicklers = scala.pickling.pickler.PrimitiveArrayPicklers
  type RefPicklers = scala.pickling.pickler.RefPicklers

  implicit def optionPickler[A: FastTypeTag](implicit elemPickler: Pickler[A], elemUnpickler: Unpickler[A], collTag: FastTypeTag[Option[A]]): Pickler[Option[A]] with Unpickler[Option[A]] =
    new Pickler[Option[A]] with Unpickler[Option[A]] {
      private implicit val elemTag = implicitly[FastTypeTag[A]]
      val tag = implicitly[FastTypeTag[Option[A]]]
      private val isPrimitive = elemTag.isEffectivelyPrimitive
      private val nullTag = implicitly[FastTypeTag[Null]]
      def pickle(coll: Option[A], builder: PBuilder): Unit = {
        // Here we cheat the "entry" so that the notion of option
        // is erased for "null"
        coll match {
          case Some(elem) =>
            builder.hintTag(elemTag)
            builder.hintStaticallyElidedType()
            elemPickler.pickle(elem, builder)
          case None =>
            builder.hintTag(nullTag)
            builder.hintStaticallyElidedType()
            elemPickler.pickle(null.asInstanceOf[A], builder)
        }
      }
      def unpickle(tag: String, preader: PReader): Any = {
        // Note - if we call beginEntry we should see JNothing or JNull show up if the option is empty.
        val reader = preader.beginCollection()
        preader.pushHints()
        if (isPrimitive) {
          reader.hintStaticallyElidedType()
          reader.hintTag(elemTag)
          reader.pinHints()
        } else reader.hintTag(elemTag)
        val length = reader.readLength
        val result: Option[A] =
          if (length == 0) None
          else {
            val elem = elemUnpickler.unpickleEntry(reader.readElement())
            Some(elem.asInstanceOf[A])
          }
        if (isPrimitive) preader.unpinHints()
        preader.popHints()
        reader.endCollection()
        result
      }
    }

  def toPickle(self: Payload) = self match {
    case p: Catalog => p.pickle
  }
}

trait JSendData

case class BareStringData(data: String) extends JSendData

case class BareObjectData[T <: Payload](data: T) extends JSendData

case class BareArrayData[T <: Payload](data: Seq[T]) extends JSendData

case class MapObjectData[T <: Payload](data: Map[String, T]) extends JSendData

case class MapArrayData[T <: Payload](data: Map[String, Seq[T]]) extends JSendData

object JSendStatus {
  def getFromCode(code: String): Option[JSendStatus] = values.find(_.code == code)

  case object Success extends JSendStatus("success")

  case object Error extends JSendStatus("error")

  case object Fail extends JSendStatus("fail")

  val values = Array(Success, Error, Fail)
}

sealed abstract class JSendStatus(val code: String) {
  val name = toString
}

//refer to JSend standard in order to see how to populate this class
case class JSend(status: JSendStatus, data: JSendData = null, message: Option[String] = None, code: Option[Int] = None) {
  def toJson(): JsValue = {
    implicit val mapStringPayload: Writes[JSendData] = new Writes[JSendData] {

      def writes(p: JSendData) = p match {
        case v: BareStringData => JsString(v.data.toString)
        case v: BareObjectData[_] => v.data.toJson
        case v: BareArrayData[_] => JsArray(v.data.map(_.toJson))
        case v: MapObjectData[_] => JsObject(v.data.map(t => t._1 -> t._2.toJson))
        case v: MapArrayData[_] => JsObject(v.data.map(t => t._1 -> JsArray(t._2.map(_.toJson))))
        case _ => JsNull
      }
    }

    implicit val jSendStatus: Writes[JSendStatus] = new Writes[JSendStatus] {
      def writes(j: JSendStatus) = JsString(j.code)
    }

    implicit val jSendToJson: Writes[JSend] = (
      (JsPath \ "status").write[JSendStatus] and
      (JsPath \ "data").write[JSendData] and
      (JsPath \ "message").writeNullable[String] and
      (JsPath \ "code").writeNullable[Int]
    )(unlift(JSend.unapply))

    Json.toJson(this)
  }
}
