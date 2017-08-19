package controllers.security

import java.util.UUID
import javax.inject.Inject

import com.example.system.modules.{ BasicComponents, TrackingManager }
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import controllers._
import forms.SignUpForm
import models.{ Roles, User }
import models.services.{ AuthTokenService, UserService }
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.Controller
import utils.auth.DefaultEnv
import utils.tracking.TrackingEvent

import scala.concurrent.Future

/**
 * The `Sign Up` controller.
 *
 * @param messagesApi            The Play messages API.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 * @param webJarAssets           The webjar assets implementation.
 */
class SignUpController @Inject() (
  val basicComponents: BasicComponents,
  val messagesApi: MessagesApi,
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient,
  trackingManager: TrackingManager,
  implicit val webJarAssets: WebJarAssets)
  extends BasicController with I18nSupport {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.security.signUp(SignUpForm.form)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit = Action.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(Ok(JSend(status = JSendStatus.Error, message = Some("Fill in all fields of the form.")).toJson())),
      data => {
        val result = Ok(JSend(status = JSendStatus.Success, data = BareStringData(Messages("sign.up.email.sent", data.email))).toJson())
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            val url = controllers.routes.ApplicationController.index().absoluteURL()
            mailerClient.send(Email(
              subject = Messages("email.already.signed.up.subject"),
              from = Messages("email.from"),
              to = Seq(data.email),
              bodyText = Some(views.txt.security.emails.alreadySignedUp(user, url).body),
              bodyHtml = Some(views.html.security.emails.alreadySignedUp(user, url).body)
            ))

            Future.successful(result)
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            val user = User(
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = None,
              lastName = None,
              fullName = None,
              email = Some(data.email),
              avatarURL = None,
              activated = false,
              roles = List(Roles.User),
              permissions = Nil
            )
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.userID)
            } yield {
              val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
              mailerClient.send(Email(
                subject = Messages("email.sign.up.subject"),
                from = Messages("email.from"),
                to = Seq(data.email),
                bodyText = Some(views.txt.security.emails.signUp(user, url).body),
                bodyHtml = Some(views.html.security.emails.signUp(user, url).body)
              ))

              trackingManager.trackEvent(TrackingEvent.SIGNUP_DONE, Map("userId" -> user.userID.toString))
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              Ok(JSend(status = JSendStatus.Success).toJson())
            }
        }
      }
    )
  }
}
