package utils.es

import javax.inject.Inject

import com.example.system.modules.EsService
import com.sksamuel.elastic4s.http.ElasticDsl._
import play.api.Environment

import scala.io.Source

/**
 * Created by stefanrmeier 2017/05/30.
 */
class Elastic4sUtils @Inject() (esService: EsService, environment: Environment) {

  lazy val client = esService.esClient
  final val ES_INDEX = "example"

  def dropSchema(): Unit = {
    client.execute {
      deleteIndex(ES_INDEX)
    }.await
  }

  def createSchema(): Unit = {

    environment
      .resourceAsStream("es_schema.json")
      .map(Source.fromInputStream)
      .map(_.mkString)
      .foreach(s => client.execute {
        createIndex(ES_INDEX).source(s)
      })
  }

}
