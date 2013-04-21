import org.specs2.mutable._


class UrlFormSpec extends Specification {

  import shortenerApp.ShortenerApp.UrlForm

  "Url form" should {

    "require url field" in {
      val form = UrlForm.bind(Map.empty[String, String])

      form.hasErrors must beTrue
      form.errors.size must equalTo(1)

      form("url").hasErrors must beTrue

      form.value must beNone
    }

    "be filled" in {
      val form = UrlForm.bind(Map("url" -> "http://google.com"))

      form.hasErrors must beFalse

      form.data must havePair("url" -> "http://google.com")

      form("url").value must beSome.which(_ == "http://google.com")

      form.value must beSome.which {
        _ == "http://google.com"
      }
    }
  }

}