package controllers

import javax.inject._

import com.example.system.modules.{ BasicComponents, TrackingManager }
import play.api.Configuration
import utils.tracking.{ TrackingBeacon, TrackingEvent }

/**
 * Created by stefanmeier on 2017/02/23.
 */
@Singleton
class TrackingController @Inject() (
  configuration: Configuration,
  val basicComponents: BasicComponents,
  trackingManager: TrackingManager
) extends BasicController {

  def track = TrackedUserAwareAction { implicit request =>
    val res = for {
      json <- request.request.body.asJson
      tb <- json.validate[TrackingBeacon].asOpt
      te <- TrackingEvent.fromCode(tb.event)
    } yield {
      trackingManager.trackEvent(te)

      Ok(JSend(status = JSendStatus.Success).toJson())
    }

    res.getOrElse({
      Ok(JSend(status = JSendStatus.Error, message = Some("Form data is invalid")).toJson())
    })
  }
}