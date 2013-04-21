package models

import org.specs2.mutable.Specification

class InMemoryShortenerSpec extends Specification {

  val Url1 = "http://pellucid.com"
  val Url2 = "http://google.com"
  val BadUrl = "ftp://badurl.com"

  val shortener = new InMemoryShortener {}

  "InMemoryShortener" should {
    "return None for not shortened url" in {
      shortener.find(Url1) must beNone
      shortener.find(Url2) must beNone
    }

    "fail to shortenUrl bad url" in {
      shortener.shorten(BadUrl).isFailure must beTrue
    }

    "shortenUrl good urls" in {
      val shorten1 = shortener.shorten(Url1)
      shorten1.toOption must equalTo(Some("0"))

      val shorten2 = shortener.shorten(Url2)
      shorten2.toOption must equalTo(Some("1"))
    }

    "return the same shortened for same url's" in {
      val shorten1 = shortener.shorten(Url1)
      val shorten2 = shortener.shorten(Url1)
      shorten1 must equalTo(shorten2)
    }
  }
}