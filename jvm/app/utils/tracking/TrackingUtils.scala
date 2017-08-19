package utils.tracking

import java.util

import com.brsanthu.googleanalytics.{ DefaultRequest, EventHit, GoogleAnalytics, GoogleAnalyticsConfig }
import com.mixpanel.mixpanelapi.{ ClientDelivery, MessageBuilder, MixpanelAPI }
import org.json.JSONObject
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json.{ JsPath, JsValue, Json, Reads, Writes }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by stefanrmeier 2017/03/16.
 */
object TrackingEvent {
  val VISITOR_KEY = "visitor"
  val TRACKING_COOKIE_KEY = "TKN"
  val MARKETING_COOKIE_KEY = "MKT"

  case object LOGIN_INIT extends TrackingEvent("LOGIN_INIT")
  case object LOGIN_DONE extends TrackingEvent("LOGIN_DONE")
  case object SIGNUP_INIT extends TrackingEvent("SIGNUP_INIT")
  case object SIGNUP_DONE extends TrackingEvent("SIGNUP_DONE")
  case object ACTIVATION_DONE extends TrackingEvent("ACTIVATION_DONE")
  case object LP_INIT extends TrackingEvent("LP_INIT")
  case object BASICINFO_INIT extends TrackingEvent("BASICINFO_INIT")
  case object WIZARD_BI extends TrackingEvent("WIZARD_BI")
  case object BASICINFO_DONE extends TrackingEvent("BASICINFO_DONE")
  case object CV_INIT extends TrackingEvent("CV_INIT")
  case object CV_DONE extends TrackingEvent("CV_DONE")
  case object JOB_SEARCH extends TrackingEvent("JOB_SEARCH")
  case object JOB_VIEW extends TrackingEvent("JOB_VIEW")
  case object PURCHASE_INIT extends TrackingEvent("PURCHASE_INIT")
  case object PURCHASE_DONE extends TrackingEvent("PURCHASE_DONE")
  case object TRACKING_TEST extends TrackingEvent("TRACKING_TEST")
  val values = List(
    LOGIN_INIT,
    LOGIN_DONE,
    LP_INIT,
    SIGNUP_INIT,
    SIGNUP_DONE,
    ACTIVATION_DONE,
    BASICINFO_INIT,
    WIZARD_BI,
    BASICINFO_DONE,
    CV_INIT,
    CV_DONE,
    JOB_SEARCH,
    JOB_VIEW,
    PURCHASE_INIT,
    PURCHASE_DONE,
    TRACKING_TEST)

  def fromCode(code: String): Option[TrackingEvent] = values.find(_.code == code)
}

abstract class TrackingEvent(val code: String) {
  val name = toString
}

trait TrackingPoster {
  def postAsync(
    msg: TrackingInfo,
    mixpanelTrackingMessageFactory: MixpanelTrackingMessageFactory,
    googleAnalyticsTrackingMessageFactory: GoogleAnalyticsTrackingMessageFactory
  ): Unit = {
    val logger: Logger = LoggerFactory.getLogger(classOf[TrackingPoster])
    val mpm = mixpanelTrackingMessageFactory(msg)
    val mpf = Future {
      mixpanelTrackingMessageFactory.mixpanel.deliver(mpm.msg)
    }

    val gpm = googleAnalyticsTrackingMessageFactory(msg)
    val gaf = gpm.ga.postAsync(gpm.msg)

    mpf.onSuccess {
      case s => {
        logger.debug("Mixpanel sent confirmation ######################################################################")
        logger.debug(msg.eventName)
        logger.debug("####################################################################################################")
      }
    }
  }
}

case class TrackingInfo(
  distinctId: String,
  eventName: String,
  trackingDateTime: String,
  userAgent: String,
  userLocale: String,
  referer: String,
  requestPath: String,
  ip: String,
  utm_source: String,
  utm_medium: String,
  utm_campaign: String,
  paramMap: Map[String, String]
) {

}

//class TrackingInfoFactory {
//
//  val trackingCookieProtocol = {
//
//    import scala.pickling._
//
//    new pickler.PrimitivePicklers with pickler.RefPicklers with json.JsonFormats {
//      // Manually generate pickler for Apple
//      implicit val applePickler = PicklerUnpickler.generate[TrackingCookie]
//      // Don't fall back to runtime picklers
//      implicit val so = static.StaticOnly
//
//      // Provide custom functions
//      def toJsonString[A: Pickler](a: A): String =
//        functions.pickle(a).value
//
//      def fromJsonString[A: Unpickler](s: String): A =
//        functions.unpickle[A](json.JSONPickle(s))
//    }
//  }
//
//  val format = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
//  //Mixpanel needs dateformat to be in GMT
//  format.setTimeZone(TimeZone.getTimeZone("GMT"))
//
//  def apply[A](req: Request[A], distinctId: String, eventName: String): TrackingInfo = {
//
//    val ip = req.headers.getById("X-Forwarded-For").getOrElse(req.remoteAddress) match {
//      case ips if ips.contains(",") => ips.substring(0, ips.indexOf(","))
//      case ips => ips
//    }
//
//    import trackingCookieProtocol._
//    val mkt = req.cookies.getById("MKT").map(_.value).getOrElse("")
//    //      or(S.responseCookies.filter(_.name == "MKT").headOption).flatMap(_.value).
//    val tc: TrackingCookie = fromJsonString(mkt)
//
//
//    TrackingInfo(
//      distinctId,
//      eventName,
//      format.format(Calendar.getInstance().getTime()),
//      req.headers.getById("User-Agent").getOrElse("undefined"),
//      "", //req.locale.openOr("undefined").toString,
//      req.headers.getById("referer").getOrElse(null),
//      req.uri,
//      ip,
//      "", //tc.utm_source.getOrElse(""),
//      "", //tc.utm_medium.getOrElse(""),
//      "" //tc.utm_campaign.getOrElse("")
//    )
//  }
//}

abstract class TrackingMessage {
}

case class MixpanelTrackingMessaage(
  msg: ClientDelivery
) extends TrackingMessage

case class GoogleAnalyticsTrackingMessage(
  msg: EventHit,
  ga: GoogleAnalytics
) extends TrackingMessage

trait TrackingMessageFactory[T <: TrackingMessage] {
  def apply(ti: TrackingInfo): T
}

trait MixpanelTrackingMessageFactory extends TrackingMessageFactory[MixpanelTrackingMessaage] {
  val logger: Logger
  val mixpanelId: String
  val mixpanel: MixpanelAPI

  def apply(msg: TrackingInfo): MixpanelTrackingMessaage = {

    val delivery: ClientDelivery = new ClientDelivery()
    val messageBuilder: MessageBuilder = new MessageBuilder(mixpanelId)

    val properties = new util.HashMap[String, Object]
    properties.put("custom-tracking-datetime", msg.trackingDateTime)
    properties.put("custom-user-agent", msg.userAgent)
    properties.put("custom-user-locale", msg.userLocale)
    properties.put("custom-user-ip", msg.ip)
    properties.put("custom-request-path", msg.requestPath)
    properties.put("custom-referer", msg.referer)
    properties.put("ip", msg.ip)
    //    properties.put("$city", "Tokyo")
    //    properties.put("$region", "Tokyo")
    //    properties.put("mp_country_code", "Japan")
    //    properties.put("$browser", "Chrome")
    //    properties.put("$browser_version", "32")
    properties.put("$current_url", msg.requestPath)
    //    properties.put("$device", "")
    //    properties.put("$initial_referrer", "google.com")
    //    properties.put("$initial_referring_domain", "google.com")
    //    properties.put("$os", "Mac OS X")
    //    properties.put("$referrer", "")
    //    properties.put("$referring_domain", "")
    //    properties.put("$screen_height", "1200px")
    //    properties.put("$screen_width", "800px")
    properties.put("utm_source", msg.utm_source)
    properties.put("utm_medium", msg.utm_medium)
    properties.put("utm_campaign", msg.utm_campaign)

    msg.paramMap.foreach(v => {
      properties.put(v._1, v._2)
    })

    val message: JSONObject = messageBuilder.event(msg.distinctId, "ss-" + msg.eventName, new JSONObject(properties))

    logger.debug("Mixpanel Event ######################################################################")
    logger.debug(message.toString)
    logger.debug(" ######################################################################")

    delivery.addMessage(message)

    MixpanelTrackingMessaage(delivery)
  }
}

trait GoogleAnalyticsTrackingMessageFactory extends TrackingMessageFactory[GoogleAnalyticsTrackingMessage] {
  val logger: Logger
  val gaId: String

  def apply(msg: TrackingInfo): GoogleAnalyticsTrackingMessage = {
    val defaultRequest = new DefaultRequest()
    defaultRequest.trackingId(gaId)
    defaultRequest.userId(msg.distinctId)
    defaultRequest.userIp(msg.ip)
    defaultRequest.userAgent(msg.userAgent)

    val config = new GoogleAnalyticsConfig()

    val ga = new GoogleAnalytics(config, defaultRequest)
    val eh = new EventHit("Lead", msg.eventName, "", 0)

    logger.debug("GoogleAnalytics Event ######################################################################")
    logger.debug("DefaultRequest.trackingId: " + defaultRequest.trackingId)
    logger.debug("DefaultRequest.userId: " + defaultRequest.userId)
    logger.debug("DefaultRequest.userIp: " + defaultRequest.userIp)
    logger.debug("DefaultRequest.userAgent: " + defaultRequest.userAgent)
    logger.debug("EventHit.eventAction: " + eh.eventAction())
    logger.debug("EventHit.eventCategory: " + eh.eventCategory())
    logger.debug("EventHit.eventLabel: " + eh.eventLabel())
    logger.debug("EventHit.eventValue: " + eh.eventValue())
    logger.debug(" ######################################################################")
    GoogleAnalyticsTrackingMessage(eh, ga)
  }

}

//class TrackingModule extends AbstractModule with AkkaGuiceSupport {
//  def configure(): Unit = {
//    bindActor[TrackingActor]("tracking-actor")
//    bind(classOf[TrackingInfoFactory]).to(classOf[TrackingInfoFactory])
//  }
//}
//
//object TrackingActor {
//  private val mixpanel: MixpanelAPI = new MixpanelAPI()
//  private val logger: Logger = LoggerFactory.getLogger(TrackingActor.getClass)
//  case object GetConfig
//
//}
//
//class TrackingActor @Inject() (configuration: Configuration) extends Actor with TrackingPoster {
//  import TrackingActor._
//
//  object MixpanelTrackingMessageFactory extends MixpanelTrackingMessageFactory {
//    override val mixpanel: MixpanelAPI = new MixpanelAPI()
//    override val logger: Logger = LoggerFactory.getLogger(MixpanelTrackingMessageFactory.getClass)
//    val mixpanelId: String = configuration.getString("tracking.mixpanelid").getById
//
//  }
//  object GoogleAnalyticsTrackingMessageFactory extends GoogleAnalyticsTrackingMessageFactory {
//    override val logger: Logger = LoggerFactory.getLogger(GoogleAnalyticsTrackingMessageFactory.getClass)
//    val gaId: String = configuration.getString("tracking.gaid").getById
//  }
//
//  val config = configuration.getString("my.config").getOrElse("none")
//
//  def receive: PartialFunction[Any, Unit] = {
//    case msg: TrackingInfo => postAsync(msg, MixpanelTrackingMessageFactory, GoogleAnalyticsTrackingMessageFactory)
//    case GetConfig =>
//      sender() ! config
//  }
//}
//

case class TrackingBeacon(event: String)

object TrackingBeacon {
  implicit val tcWrites = new Writes[TrackingBeacon] {
    def writes(tc: TrackingBeacon): JsValue = Json.obj(
      "eventName" -> tc.event
    )
  }

  implicit val tcReads: Reads[TrackingBeacon] = ((JsPath \ "eventName").read[String]).map(TrackingBeacon.apply)
}

case class MarketingCookie(utm_source: Option[String], utm_medium: Option[String], utm_campaign: Option[String])

object MarketingCookie {
  implicit val mcWrites = new Writes[MarketingCookie] {
    def writes(mc: MarketingCookie): JsValue = Json.obj(
      "utm_source" -> mc.utm_source,
      "utm_medium" -> mc.utm_medium,
      "utm_campaign" -> mc.utm_campaign
    )
  }

  import play.api.libs.functional.syntax._

  implicit val mcReads: Reads[MarketingCookie] = (
    (JsPath \ "utm_source").readNullable[String] and
    (JsPath \ "utm_medium").readNullable[String] and
    (JsPath \ "utm_campaign").readNullable[String]
  )(MarketingCookie.apply _)
}