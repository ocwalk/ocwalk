package ocwalk

import ocwalk.conf.{JsReader, OcwalkConfig}
import ocwalk.util.global.GlobalContext
import ocwalk.util.http._
import ocwalk.util.logging.Logging
import org.scalajs.dom._

import scala.concurrent.Future

/** Starts the UI application */
//noinspection TypeAnnotation
object app extends App with GlobalContext with Logging {
  override protected def logKey: String = "app"

  config.setGlobalReader(JsReader)
  implicit val conf: OcwalkConfig = ocwalk.conf.Config

  window.location.pathname match {
    case discord if discord.startsWith("/discord") =>
      queryParameter("code") match {
        case Some(code) => loginDiscord(code)
        case None =>
          log.warn("login error, redirecting to [/]")
          redirect("/")
      }
    case _ =>
    // startOcwalk("/")
  }

  def loginDiscord(code: String)(implicit config: OcwalkConfig): Unit = for {
    user <- Future.successful(None) // post[LoginDiscord, User]("/api/discord", LoginDiscord(code))
    _ = log.info(s"logged in as [$user]")
    _ = redirectSilent("/", preserveQuery = false)
    // _ = startOcwalk("/")
  } yield user

}