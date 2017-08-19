package modules

import org.specs2.mutable.Specification
import play.api.Mode
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.inject.bind

/**
 * Created by stefanrmeier 2017/03/29.
 */
class MockMailgunMailerTest extends Specification {

  "Mailgun should mock" in {

    val injector = new GuiceApplicationBuilder()
      .in(Mode.Test)
      // .overrides(bind[MailerClient].to[MockMailgunMailer])
      .injector

    val mailer = injector.instanceOf[MailerClient]

    //val email = Email("test", "aalexelis@gmail.com", bodyText = Option("TEST"))
    val email = Email(subject = "test", from = "sysadm@example.com", to = Seq("mailgun.test@example.com"), bodyText = Option("TEST"), bodyHtml = Option("<h1>TEST</h1>"))

    val res = mailer.send(email)
    println(res)

    1 shouldEqual 1

  }

}
