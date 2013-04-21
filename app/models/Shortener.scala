package models

import scalaz.Scalaz._
import scalaz.Validation
import org.apache.commons.validator.routines.{UrlValidator => CommonsValidator}
import scala.concurrent.stm._

trait Shortener {
  type UrlError = String
  type UrlShortened = String

  def shorten(url: String): Validation[UrlError, UrlShortened]

  def find(shortened: String): Option[String]
}

trait InMemoryShortener extends Shortener {

  import UrlValidator._

  private[this] val shortenedStream = Ref(ShortenedStream())
  private[this] val shorten2url = Ref(Map[String, String]())
  private[this] val url2shorten = Ref(Map[String, String]())

  def shorten(url: String) = validateUrl(url).map(validUrl =>
    atomic {
      implicit txn =>
        url2shorten.get.get(validUrl) getOrElse {
          val shortened = shortenedStream.get.head
          shortenedStream.transform(_.tail)
          shorten2url.transform(_ + (shortened -> url))
          url2shorten.transform(_ + (url -> shortened))
          shortened
        }
    }
  )

  def find(shortened: String) = {
    val view = shorten2url.single
    view() get (shortened)
  }
}

object UrlValidator {
  private[this] val Schemes = Array("http", "https")
  private[this] val UrlValidator = new CommonsValidator(Schemes)
  private[this] val InvalidUrlMessage = "Invalid Url"

  def validateUrl(url: String): Validation[String, String] = {
    if (UrlValidator.isValid(url))
      url.success
    else
      InvalidUrlMessage.fail
  }

}

object ShortenedStream {
  val DefaultCharacters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

  def apply(characters: String = DefaultCharacters): Stream[String] = start(characters)

  def start(characters: String = DefaultCharacters): Stream[String] = {
    resume(characters(0).toString)(characters)
  }

  def resume(from: String)(characters: String = DefaultCharacters): Stream[String] = {
    // Assert that all characters are unique
    assert(!characters.isEmpty)
    assert(characters.toSet.size == characters.length)

    buildStream(from, characters)
  }

  private[this] def buildStream(head: => String, characters: String): Stream[String] = {
    def next: String = {
      val charIndexes = head.map(characters.indexOf(_))
      val lastAvailableForIncrement = charIndexes.lastIndexWhere(_ < characters.length - 1)

      val nextIndexes = if (lastAvailableForIncrement == -1) {
        List.fill(charIndexes.size + 1)(0)
      } else {
        val slice1 = charIndexes.slice(0, lastAvailableForIncrement)
        val slice2 = (charIndexes(lastAvailableForIncrement) + 1) :: Nil
        val slice3 = List.fill(charIndexes.size - lastAvailableForIncrement - 1)(0)
        slice1 ++ slice2 ++ slice3
      }

      nextIndexes.map(characters(_)).mkString
    }

    head #:: buildStream(next, characters)
  }
}

