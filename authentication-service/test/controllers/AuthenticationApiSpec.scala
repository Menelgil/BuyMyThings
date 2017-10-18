package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

class AuthenticationApiSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {
  private val EMAIL = "email@domain.ltd"
  private val PASSWORD = "password"

  "Authentication API GET /api/account/authenticate" should {
    "return NotFound when account does not exists" in {

      val controller = inject[AuthenticationApi]
      val response = controller.authenticate(EMAIL, PASSWORD)(FakeRequest(GET, "/api/account/authenticate"))
      status(response) mustBe NOT_FOUND
    }

    "return Ok when account exists and the password matches the email" in {

    }

    "return InternalServerError when an error occurs" in {

    }
  }
}
