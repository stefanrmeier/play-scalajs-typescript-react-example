package modules

import javax.inject.Inject

import awscala.s3.S3
import awscala.{ Credentials, CredentialsProvider, Region }
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

/**
 * Created by stefanrmeier 2017/06/01.
 */
class AWSModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[CredentialsProvider].to[CredentialsProviderImp].asEagerSingleton()
    bind[Aws].to[AwsImpl].asEagerSingleton()
  }

}

class CredentialsProviderImp @Inject() (configuration: Configuration) extends CredentialsProvider {
  override def refresh(): Unit = {}
  override def getCredentials(): Credentials = new Credentials(
    configuration.getString("aws.accessKeyId").getOrElse("key"), //TODO
    configuration.getString("aws.secretKey").getOrElse("key") //TODO
  )
}

trait Aws {
  val s3: S3
  val s3endpoint: String
}

class AwsImpl @Inject() (credentialsProvider: CredentialsProvider, configuration: Configuration) extends Aws {
  val awsRegion: Region = configuration.getString("aws.s3.region").map(n => Region(n)).getOrElse(Region.Tokyo)
  import scala.collection.JavaConversions._

  override val s3endpoint: String = awsRegion.getAvailableEndpoints.filter(_.contains("s3")).head
  override lazy val s3: S3 = S3(credentialsProvider).at(awsRegion)
}
