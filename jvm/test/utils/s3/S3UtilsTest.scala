package utils.s3

import java.io.File

import org.specs2.mutable.Specification
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * Created by stefanrmeier 2017/06/06.
 */
class S3UtilsTest extends Specification {
  private val ROOTDIR = "/var/example" + "/"

  val injector = new GuiceApplicationBuilder()
    .injector

  val s3Utils = injector.instanceOf[S3Utils]

  "show getObjectSummaries" in {
    val s = s3Utils.getObjectSummaries(s3Utils.S3Bucket.get)
    println(s)
    s.foreach(s => println(s"${s3Utils.urlPrefixS3}/${s}"))
    1 mustEqual (1)
  }

  "put a file" in {
    val f = new File(ROOTDIR, "test.zip")

    if (f.canRead) {
      val por = s3Utils.put("id", f.getName, None, f, s3Utils.S3Bucket.get, s3Utils.urlPrefixS3)
      println(por)
    }

    1 mustEqual (1)
  }

  "getById a file" in {

    val por = s3Utils.get("id", "test.zip", None, null, s3Utils.S3Bucket.get, s3Utils.urlPrefixS3)
    println(por)
    println(por.map(_.filename))

    1 mustEqual (1)
  }

}
