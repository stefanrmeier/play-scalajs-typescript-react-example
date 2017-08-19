package controllers.security

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import com.example.system.modules.{ BasicComponents, TrackingManager }
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.{ BasicController, WebJarAssets }
import models.services.{ AuthTokenService, UserService }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{ Email, MailerClient }
import utils.tracking.TrackingEvent

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * The `Activate Account` controller.
 *
 * @param messagesApi      The Play messages API.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param webJarAssets     The WebJar assets locator.
 */
class ActivateAccountController @Inject() (
  val messagesApi: MessagesApi,
  userService: UserService,
  val basicComponents: BasicComponents,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient,
  trackingManager: TrackingManager,
  implicit val webJarAssets: WebJarAssets)
  extends BasicController with I18nSupport {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit request =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(routes.SignInController.view()).flashing("info" -> Messages("activation.email.sent", decodedEmail))

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userID).map { authToken =>
          val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()

          mailerClient.send(Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.security.emails.activateAccount(user, url).body),
            bodyHtml = Some(views.html.security.emails.activateAccount(user, url).body)
          ))
          result
        }
      case None => Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID) = Action.async { implicit request =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            trackingManager.trackEvent(TrackingEvent.ACTIVATION_DONE, Map("userId" -> user.userID.toString))
            Redirect(controllers.routes.ApplicationController.index().absoluteURL()).flashing("fragment" -> "/signup/done")
          }
        case _ => Future.successful(BadRequest)
      }
      case None => Future.successful(BadRequest)
    }
  }
}
