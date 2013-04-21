package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import models.Shortener

trait ShortenerApp extends Controller {
  self: Shortener =>

  private val ShortenedNotFound = "Shortened url not found"

  val UrlForm = Form(
    "url" -> nonEmptyText
  )

  def index = Action {
    Ok(html.index(UrlForm))
  }

  def shortenUrl = Action {
    implicit request =>

      def handleErrors(formWithErrors: Form[String]) = {
        BadRequest(html.index(formWithErrors))
      }

      def handleUrl(url: String) = {
        shorten(url).fold(
          failure => handleErrors(UrlForm.fill(url).withError("url", failure)),
          success => Ok(html.shortened(url, success))
        )
      }

      UrlForm.bindFromRequest.fold(handleErrors, handleUrl)
  }

  def redirect(shortened: String) = Action {
    _ =>
    // It would be better to use MovedPermanently instead of TemporaryRedirect,
    // however redirects are cached during testing
      find(shortened).map(TemporaryRedirect(_)) getOrElse {
        Ok(html.index(UrlForm.withError("url", ShortenedNotFound)))
      }
  }
}
