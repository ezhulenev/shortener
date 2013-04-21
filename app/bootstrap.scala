import controllers.ShortenerApp
import models.InMemoryShortener

package object shortenerApp {
  // Good place for DI different shortener
  val ShortenerApp = new ShortenerApp with InMemoryShortener
}