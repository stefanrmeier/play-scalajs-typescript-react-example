package com.example.system.modules

import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.{ Calendar, TimeZone }
import javax.inject.Inject

import com.mixpanel.mixpanelapi.MixpanelAPI
import com.mohiva.play.silhouette.api.Silhouette
import controllers.TrackedRequest
import org.slf4j.{ Logger, LoggerFactory }
import play.api.{ Configuration, Environment }
import play.api.inject.Module
import play.api.mvc.Request
import utils.auth.DefaultEnv
import utils.tracking._

/**
 * Created by stefanrmeier 2017/03/17.
 */
class BasicComponentsModule extends Module {
  def bindings(env: Environment, config: Configuration) = Seq(
    bind[TrackingManager].to[TrackingManagerImpl],
    bind[MixpanelTrackingMessageFactory].to[MixpanelTrackingMessageFactoryImpl],
    bind[GoogleAnalyticsTrackingMessageFactory].to[GoogleAnalyticsTrackingMessageFactoryImpl],
    bind[BasicComponents].toSelf.eagerly //to[EsServiceImpl].eagerly
  )
}

class BasicComponents @Inject() (
  val mixpanelTrackingMessageFactory: MixpanelTrackingMessageFactory,
  val googleAnalyticsTrackingMessageFactory: GoogleAnalyticsTrackingMessageFactory,
  val silhouette: Silhouette[DefaultEnv]
)

class MixpanelTrackingMessageFactoryImpl @Inject() (configuration: Configuration) extends MixpanelTrackingMessageFactory {
  override val mixpanel: MixpanelAPI = new MixpanelAPI()
  override val logger: Logger = LoggerFactory.getLogger(classOf[MixpanelTrackingMessageFactory])
  val mixpanelId: String = configuration.getString("tracking.mixpanelid").get
}

class GoogleAnalyticsTrackingMessageFactoryImpl @Inject() (configuration: Configuration) extends GoogleAnalyticsTrackingMessageFactory {
  override val logger: Logger = LoggerFactory.getLogger(classOf[GoogleAnalyticsTrackingMessageFactory])
  val gaId: String = configuration.getString("tracking.gaid").get
}

trait TrackingManager {
  def trackEvent[A](event: TrackingEvent, paramMap: Map[String, String] = Map())(implicit request: TrackedRequest[A, _]): Unit = {
  }
}

class TrackingManagerImpl @Inject() (
  mixpanelTrackingMessageFactory: MixpanelTrackingMessageFactory,
  googleAnalyticsTrackingMessageFactory: GoogleAnalyticsTrackingMessageFactory
) extends TrackingManager with TrackingPoster {
  val format = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
  //Mixpanel needs dateformat to be in GMT
  format.setTimeZone(TimeZone.getTimeZone("GMT"))

  override def trackEvent[A](event: TrackingEvent, paramMap: Map[String, String] = Map())(implicit request: TrackedRequest[A, _]): Unit = {
    for {
      distinctId <- request.tknCookies.find(_.name == TrackingEvent.TRACKING_COOKIE_KEY).map(_.value)
    } yield {

      val ip = request.headers.get("X-Forwarded-For").getOrElse(request.remoteAddress) match {
        case ips if ips.contains(",") => ips.substring(0, ips.indexOf(","))
        case ips => ips
      }
      import play.api.libs.json._
      val mc: Option[MarketingCookie] = request.tknCookies.find(_.name == TrackingEvent.MARKETING_COOKIE_KEY).map(_.value).
        map(mktJson => Json.parse(URLDecoder.decode(mktJson, "utf-8")).validate[MarketingCookie] match {
          case JsSuccess(c, _) => c
          case _ => throw new RuntimeException("unparsable json")
        })

      val msg = TrackingInfo(
        distinctId,
        event.code,
        format.format(Calendar.getInstance().getTime()),
        request.headers.get("User-Agent").getOrElse("undefined"),
        "", //req.locale.openOr("undefined").toString,
        request.headers.get("referer").getOrElse(null),
        request.uri,
        ip,
        mc.flatMap(_.utm_source).getOrElse(""),
        mc.flatMap(_.utm_medium).getOrElse(""),
        mc.flatMap(_.utm_campaign).getOrElse(""),
        paramMap
      )

      postAsync(msg, mixpanelTrackingMessageFactory, googleAnalyticsTrackingMessageFactory)
    }
  }
}