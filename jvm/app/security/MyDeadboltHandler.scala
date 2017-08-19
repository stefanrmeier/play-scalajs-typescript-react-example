package security

import javax.inject.Inject

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{ AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler }
import com.google.inject.assistedinject.Assisted
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import com.mohiva.play.silhouette.api.actions.UserAwareRequestHandlerBuilder
import play.api.mvc.{ Request, Result, Results }
import models.User
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(silhouette: Silhouette[DefaultEnv], dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    // e.g. request.session.getById("user")
    //val a = silhouette.UserAwareAction(Results.Ok)
    val a = silhouette.UserAwareRequestHandler(request)({ request => Future { new HandlerResult(Results.Ok, Some(request)) } })
    a.map(h => h.data.flatMap(d => {
      d.identity
    }))
  }

  def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future { Results.Forbidden(views.html.security.accessFailed()) }
  }
}