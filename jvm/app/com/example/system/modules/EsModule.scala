package com.example.system.modules

import javax.inject.{ Inject, Singleton }

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import play.api.inject.{ ApplicationLifecycle, Module }
import play.api.{ Configuration, Environment }

import scala.concurrent.Future

/**
 * Created by stefanrmeier 2017/01/23.
 */
class EsModule extends Module {
  def bindings(env: Environment, config: Configuration) = Seq(
    bind[EsService].to[EsServiceImpl].eagerly
  )
}

//trait EsService {
//  val esUrl: String
//  val esClient: ESClient
//}
//
//@Singleton
//class EsServiceImpl @Inject() (
//  lifecycle: ApplicationLifecycle,
//  configuration: Configuration
//) extends EsService {
//
//  override lazy val esUrl = configuration.underlying.getString("es.url")
//
//  override lazy val esClient: ESClient = ESClient(esUrl)
//
//  def onStart(): Unit = {
//    // AsyncESClient.init()
//    ESClient.init()
//  }
//
//  def onStop(): Unit = {
//    //AsyncESClient.shutdown()
//    ESClient.shutdown()
//  }
//
//  lifecycle.addStopHook(() => Future.successful(onStop))
//  onStart()
//}

trait EsService {
  val esUrl: ElasticsearchClientUri
  val esClient: HttpClient
}

@Singleton
class EsServiceImpl @Inject() (
  lifecycle: ApplicationLifecycle,
  configuration: Configuration
) extends EsService {

  lazy val esHost: String = configuration.underlying.getString("es.host")
  lazy val esPort: Int = configuration.underlying.getInt("es.port")
  override lazy val esUrl: ElasticsearchClientUri = ElasticsearchClientUri(esHost, esPort)

  private var client: Option[HttpClient] = None
  override lazy val esClient: HttpClient = client.get

  def onStart(): Unit = {
    // AsyncESClient.init()
    //ESClient.init()
    client = Option(HttpClient(esUrl))
  }

  def onStop(): Unit = {
    //AsyncESClient.shutdown()
    //ESClient.shutdown()
    client.foreach(_.close)
  }

  lifecycle.addStopHook(() => Future.successful(onStop))
  onStart()
}
