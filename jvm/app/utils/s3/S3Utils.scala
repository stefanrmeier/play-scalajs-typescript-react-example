package utils.s3

import java.io.{ File, FileInputStream, FileReader, InputStream }
import java.security.MessageDigest
import javax.inject.Inject

import akka.stream.IOResult
import akka.stream.scaladsl.{ FileIO, Flow, RunnableGraph, Sink }
import akka.stream.scaladsl.StreamConverters._
import awscala.s3.{ Bucket, PutObjectResult, S3, S3Object }
import awscala.{ Credentials, CredentialsProvider, Region }
import com.amazonaws.services.s3.model.ObjectMetadata
import modules.Aws
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * Created by stefanrmeier 2017/06/01.
 */
class S3Utils @Inject() (aws: Aws) {

  //  object Resumes extends BucketS3Utils {
  //    override val BUCKET: String = "resumes"
  //  }

  implicit val s3: S3 = aws.s3

  val BUCkET = "example.resume"
  val urlPrefixS3 = aws.s3endpoint + "/" + BUCkET // http GET to this will only work for publicly accessed or when supplying credentials

  val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")

  val S3Bucket: Option[Bucket] = s3.bucket(BUCkET)

  def getObjectSummaries(resumesS3Bucket: Bucket): Seq[String] = resumesS3Bucket.objectSummaries().map(_.getKey)

  //  def toBase64Enc(is: InputStream) = {
  //    var ab = ArrayBuffer[Byte]()
  //    var c = is.read()
  //    while (c > -1) {
  //      ab += c.toByte
  //      c = is.read()
  //    }
  //
  //    Base64.encodeBase64(ab.toArray)
  //  }
  //
  //  def getS3ObjbBase64Enc(filename: String, resumesS3Bucket: Bucket): Option[Array[Byte]] = {
  //    val s3Object: Option[S3Object] = resumesS3Bucket.getObject(filename)
  //    s3Object.map(_.getObjectContent).map(toBase64Enc)
  //  }
  //
  //  def sha1(b64content: Array[Byte]): String = {
  //    val digest: Array[Byte] = MessageDigest.getInstance("MD5").digest(b64content)
  //    digest.map(b => "%02x".format(b)).mkString
  //  }
  //
  //  def content(b64content: Array[Byte]): Array[Byte] = Base64.decodeBase64(b64content)
  //
  //  def put(b64content: Array[Byte], resumesS3Bucket: Bucket): PutObjectResult = {
  //    val _content = content(b64content)
  //    val _sha1 = sha1(b64content)
  //    val _key: String =  _sha1 + ".jpg"
  //    val metadata = new ObjectMetadata()
  //    metadata.setContentType("image/jpeg")
  //    resumesS3Bucket.putObject(_key,_content, metadata)
  //  }
  //
  //
  //  def put(b64content: Array[Byte]):String = {
  //    val _sha1 = sha1(b64content)
  //    val _key = _sha1 + ".jpg"
  //    val _content = content(b64content)
  //    val metadata = new ObjectMetadata()
  //    metadata.setContentType("image/jpeg")
  //
  //    resumesS3Bucket.map(_.putObjectAsPublicRead(_key, _content, metadata)).
  //      map(o => aws.urlPrefixS3 + o.key).
  //      getOrElse("")
  //  }

  object S3ObjectKey {
    def apply(id: String, filename: String, contentType: Option[String], file: File): String = {
      val relPath = id
      s"${relPath}/${filename}"
    }

    def getIdAndFilename(key: String): Option[(String, String)] = key.split("/").toList.reverse match {
      case filename :: id :: _ => Some((id, filename))
      case _ => None
    }
  }

  def put(id: String, filename: String, contentType: Option[String], file: File, resumesS3Bucket: Bucket, urlPrefixS3: String) = {
    val key = S3ObjectKey(id, filename, contentType, file)
    s3.put(resumesS3Bucket, key, file)
  }

  //TODO be careful with what duckets are exposed to public view
  //  def putPublicUrl(id: String, filename: String, contentType: Option[String], file: File, resumesS3Bucket: Bucket, urlPrefixS3: String) = {
  //    val relPath = id
  //    val key = s"${relPath}/${filename}"
  //    val por = s3.putObjectAsPublicRead( resumesS3Bucket, key, file)
  //    s"${urlPrefixS3}/${por.bucket.name}/${por.key}"
  //  }

  def getS3Object(id: String, filename: String, contentType: Option[String], file: File, resumesS3Bucket: Bucket, urlPrefixS3: String): Option[S3Object] = {
    val key = S3ObjectKey(id, filename, contentType, file)
    getS3Object(key, resumesS3Bucket, urlPrefixS3)
  }

  def getS3Object(key: String, resumesS3Bucket: Bucket, urlPrefixS3: String): Option[S3Object] = {
    s3.get(resumesS3Bucket, key)
  }

  case class S3File(filename: String, content: InputStream, contentType: String) //TODO decide how to treat model API

  def get(id: String, filename: String, contentType: Option[String], file: File, resumesS3Bucket: Bucket, urlPrefixS3: String): Option[S3File] = {
    val s3Obj = getS3Object(id, filename, contentType, file, resumesS3Bucket, urlPrefixS3)
    s3Obj.map(o => S3File(filename, o.content, o.metadata.getContentType))
  }

  def get(key: String, resumesS3Bucket: Bucket, urlPrefixS3: String): Option[S3File] = {
    val s3ObjO = s3.get(resumesS3Bucket, key)
    for { o <- s3ObjO; (_, filename) <- S3ObjectKey.getIdAndFilename(o.key) } yield S3File(filename, o.content, o.metadata.getContentType)
  }

}
