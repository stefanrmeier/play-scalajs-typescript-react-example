package controllers

import javax.inject.Inject

import com.example.system.modules.{ BasicComponents, TrackingManager }
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import utils.auth.DefaultEnv
import utils.tracking.{ TrackingEvent }

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class ApplicationController @Inject() (
  val basicComponents: BasicComponents,
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  trackingManager: TrackingManager,
  implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport with BasicController {

  def index = TrackedUserAwareAction { implicit request =>
    Ok(views.html.index())
  }

  def appjs = TrackedUserAwareAction.async { implicit request =>
    request.cookies.get("auth") match {
      case Some(cookie) if cookie.value.nonEmpty => controllers.Assets.versioned("/public", "javascripts/dist/members.js")(request)
      case _ => controllers.Assets.versioned("/public", "javascripts/dist/public.js")(request)
    }
  }

  def javascriptRoutes = TrackedUserAwareAction { implicit request =>
    request.cookies.get("auth") match {
      case Some(cookie) if cookie.value.nonEmpty => {
        Ok(
          JavaScriptReverseRouter("jsRoutes")(
            routes.javascript.ApplicationController.signOut,
            routes.javascript.TrackingController.track,
            security.routes.javascript.ForgotPasswordController.submit,
            security.routes.javascript.ResetPasswordController.submit,
            security.routes.javascript.SignInController.submit,
            security.routes.javascript.SignUpController.submit,
            security.routes.javascript.ForgotPasswordController.submit,
            security.routes.javascript.ResetPasswordController.submit
          )
        ).as("text/javascript")
      }
      case _ => {
        Ok(
          JavaScriptReverseRouter("jsRoutes")(
            routes.javascript.ApplicationController.signOut,
            routes.javascript.TrackingController.track,
            security.routes.javascript.SignInController.submit,
            security.routes.javascript.SignUpController.submit,
            security.routes.javascript.ForgotPasswordController.submit,
            security.routes.javascript.ResetPasswordController.submit,
            security.routes.javascript.ForgotPasswordController.submit,
            security.routes.javascript.ResetPasswordController.submit
          )
        ).as("text/javascript")
      }
    }

  }

  def js(path: String) = TrackedUserAwareAction { implicit request =>
    Ok(views.html.index())
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = TrackedSecureAction.async { implicit request =>
    trackingManager.trackEvent(TrackingEvent.LOGIN_INIT)
    val result = Ok(JSend(status = JSendStatus.Success).toJson()).discardingCookies(DiscardingCookie("auth"))
    silhouette.env.eventBus.publish(LogoutEvent(request.request.identity, request))
    silhouette.env.authenticatorService.discard(request.request.authenticator, result)
  }
}
