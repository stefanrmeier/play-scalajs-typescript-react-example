package modules

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }
import java.nio.charset.Charset
import javax.inject.{ Inject, Named }
import javax.mail._
import javax.mail.internet.{ InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart }

import akka.stream.scaladsl.{ Source, StreamConverters }
import akka.util.ByteString
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import play.api.inject.Module
import play.api.libs.mailer.{ AttachmentData, AttachmentFile, Email, MailerClient }
import play.api.libs.ws._
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.{ DataPart, FilePart }
import play.api.{ Configuration, Environment, Logger }
import play.core.formatters.Multipart

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by stefanrmeier 2017/03/28.
 */
// for runtime injection
class MailgunModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[MailerClient].to[MailgunMailerClient]
  )
}

class MailgunMailerClient @Inject() (configuration: Configuration, ws: WSClient) extends MailerClient {
  lazy val isMock: Boolean = configuration.getBoolean("mailgun.mailer.mock").get
  lazy val apiBaseUrl: String = configuration.getString("mailgun.mailer.ApiBaseUrl").get
  lazy val apiDomain: String = configuration.getString("mailgun.mailer.ApiDomain").get
  lazy val apiKey: String = configuration.getString("mailgun.mailer.ApiKey").get

  lazy val mailSender: MailSender = isMock match {
    case true => new MockMailgunSender(apiBaseUrl, apiDomain, apiKey, ws)
    case _ => new MailgunSender(apiBaseUrl, apiDomain, apiKey, ws)
  }

  override def send(data: Email): String = mailSender.send(data)
}

object MailgunSender {

  def mimeMessage(email: Email): MimeMessage = {
    val mimeMessage = new MimeMessage(null.asInstanceOf[Session]) //TODO test if appropriate
    mimeMessage.setFrom(new InternetAddress(email.from))
    mimeMessage.setSubject(email.subject)
    mimeMessage.setReplyTo(email.replyTo.toArray.map(new InternetAddress(_)))
    mimeMessage.addRecipients(Message.RecipientType.TO, email.to.toArray.map(new InternetAddress(_).asInstanceOf[Address]))
    mimeMessage.addRecipients(Message.RecipientType.CC, email.cc.toArray.map(new InternetAddress(_).asInstanceOf[Address]))
    mimeMessage.addRecipients(Message.RecipientType.BCC, email.bcc.toArray.map(new InternetAddress(_).asInstanceOf[Address]))
    mimeMessage.setContent({
      val mimeMultipart = new MimeMultipart("alternative")
      email.bodyText.foreach(t =>
        mimeMultipart.addBodyPart({
          val mbp = new MimeBodyPart()
          mbp.setText(t, Charset.defaultCharset().name())
          mbp
        }))
      email.bodyHtml.foreach(h =>
        mimeMultipart.addBodyPart({
          val mbp = new MimeBodyPart()
          mbp.setText(h, Charset.defaultCharset().name(), "html")
          mbp
        })
      )
      email.attachments.foreach {
        case af: AttachmentFile =>
          mimeMultipart.addBodyPart({
            val mbp = new MimeBodyPart()
            mbp.attachFile(af.file)
            af.description.foreach(mbp.setDescription(_))
            af.disposition.foreach(mbp.setDisposition(_))
            mbp.setFileName(af.name)
            mbp
          })
        case ad: AttachmentData =>
          mimeMultipart.addBodyPart({
            val mbp = new MimeBodyPart(new ByteArrayInputStream(ad.data)) //TODO is this correct?
            ad.description.foreach(mbp.setDescription(_))
            ad.disposition.foreach(mbp.setDisposition(_))
            mbp.setFileName(ad.name)
            mbp
          })
      }
      mimeMultipart
    })
    mimeMessage
  }

  def mimeBody(email: Email): Source[MultipartFormData.Part[Source[ByteString, _]], _] = {
    def changeDomainForStaging(mailaddress: String) = mailaddress //TODO decide how to do this

    val tos: String = (email.to.toList ::: email.cc.toList ::: email.bcc.toList).
      map(changeDomainForStaging).
      mkString("", ",", "")

    Source(
      DataPart("from", email.from) ::
        DataPart("to", tos) ::
        DataPart("subject", email.subject) ::
        FilePart("message", "message", None, StreamConverters
          .fromInputStream(() => {
            val ba = new ByteArrayOutputStream()
            mimeMessage(email).writeTo(ba)
            new ByteArrayInputStream(ba.toByteArray)
          })
        ) ::
        List()
    )
  }

}

trait MailSender {
  def send(email: Email): String
}

class MockMailgunSender(
  apiBaseUrl: String,
  apiDomain: String,
  apiKey: String,
  ws: WSClient) extends MailSender {
  val apiResource = "messages.mime"

  override def send(email: Email): String = {

    val boundary = Multipart.randomBoundary()
    val contentType = s"multipart/form-data; boundary=$boundary"
    val body = MailgunSender.mimeBody(email)

    val req = ws.url(s"$apiBaseUrl/$apiDomain/$apiResource").
      withAuth("api", apiKey, WSAuthScheme.BASIC).
      withBody(StreamedBody(Multipart.transform(body, boundary))).
      withHeaders("Content-Type" -> contentType)

    Logger.info("mock implementation, send email")
    Logger.info(s"subject: ${email.subject}")
    Logger.info(s"from: ${email.from}")
    email.bodyText.foreach(bodyText => Logger.info(s"bodyText: $bodyText"))
    email.bodyHtml.foreach(bodyHtml => Logger.info(s"bodyHtml: $bodyHtml"))
    email.to.foreach(to => Logger.info(s"to: $to"))
    email.cc.foreach(cc => Logger.info(s"cc: $cc"))
    email.bcc.foreach(bcc => Logger.info(s"to: $bcc"))
    email.replyTo.foreach(replyTo => Logger.info(s"replyTo: $replyTo"))
    email.bounceAddress.foreach(bounce => Logger.info(s"bounceAddress: $bounce"))
    email.attachments.foreach(attachment => Logger.info(s"attachment: $attachment"))
    email.headers.foreach(header => Logger.info(s"header: $header"))
    Logger.info("Request that would be sent to Mailgun")
    Logger.info(req.toString)

    req.toString
  }

}

class MailgunSender(
  apiBaseUrl: String,
  apiDomain: String,
  apiKey: String,
  ws: WSClient) extends MailSender {
  val apiResource = "messages.mime"

  override def send(email: Email): String = Await.result(sendF(email), Duration.Inf) //TODO replace it with more sensible duration

  def sendF(email: Email): Future[String] = {

    val boundary = Multipart.randomBoundary()
    val contentType = s"multipart/form-data; boundary=$boundary"
    val body = MailgunSender.mimeBody(email)

    val req = ws.url(s"$apiBaseUrl/$apiDomain/$apiResource").
      withAuth("api", apiKey, WSAuthScheme.BASIC).
      withBody(StreamedBody(Multipart.transform(body, boundary))).
      withHeaders("Content-Type" -> contentType)

    val resp = req.execute("POST")
    resp.map(_.toString)
  }

}