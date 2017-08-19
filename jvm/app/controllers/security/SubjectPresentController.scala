package controllers.security

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ ActionBuilders, DeadboltActions }
import play.api.mvc.Controller
import security.HandlerKeys
import views.html.security.accessOk

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.reflectiveCalls
/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class SubjectPresentController @Inject() (deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders) extends Controller {

  def loggedIn = deadbolt.SubjectPresent()() { authRequest =>
    Future {
      Ok(accessOk())
    }
  }

  def notLoggedIn = deadbolt.SubjectNotPresent()() { authRequest =>
    Future {
      Ok(accessOk())
    }
  }

  def restrictOperator = deadbolt.Restrict(List(Array("operator")))() { authRequest =>
    Future {
      Ok(accessOk())
    }
  }

}