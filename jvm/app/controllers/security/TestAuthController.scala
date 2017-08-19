package controllers.security

import javax.inject.Inject

import com.example.system.modules.BasicComponents
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.{ BasicController, WebJarAssets }
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
 * Created by stefanrmeier 2017/02/24.
 */

/**
 * Test authentification-authorization controller.
 *
 * @param messagesApi The Play messages API.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class TestAuthController @Inject() (
  val basicComponents: BasicComponents,
  val messagesApi: MessagesApi,
  socialProviderRegistry: SocialProviderRegistry,
  implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport with BasicController {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def testauth = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.security.home(request.identity)))
  }
}
