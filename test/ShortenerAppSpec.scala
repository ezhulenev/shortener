import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ShortenerAppSpec extends Specification {

  "shortenerApp" should {

    "render an empty form on index" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
      }
    }

    "redirect by shortened url" in new WithApplication() {
      val shorten = route(FakeRequest(POST, "/").withFormUrlEncodedBody("url" -> "http://google.com")).get
      status(shorten) must equalTo(OK)

      val redirect = route(FakeRequest(GET, "/0")).get
      status(redirect) must equalTo(TEMPORARY_REDIRECT)
      redirectLocation(redirect) must beSome.which(_ == "http://google.com")
    }
  }
}
