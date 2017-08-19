package security

import javax.inject.{ Inject, Singleton }

import be.objectify.deadbolt.scala.{ DeadboltHandler, HandlerKey }
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.mohiva.play.silhouette.api.Silhouette
import utils.auth.DefaultEnv

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Singleton
class MyHandlerCache @Inject() (silhouette: Silhouette[DefaultEnv]) extends HandlerCache {

  val defaultHandler: DeadboltHandler = new MyDeadboltHandler(silhouette)

  val handlers: Map[Any, DeadboltHandler] = Map(
    HandlerKeys.defaultHandler -> defaultHandler,
    HandlerKeys.altHandler -> new MyDeadboltHandler(silhouette, Some(MyAlternativeDynamicResourceHandler)),
    HandlerKeys.userlessHandler -> new MyUserlessDeadboltHandler(silhouette))

  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
