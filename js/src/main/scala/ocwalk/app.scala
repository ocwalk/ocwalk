package ocwalk

import lib.history
import ocwalk.box._
import ocwalk.common._
import ocwalk.conf.{JsReader, OcwalkConfig}
import ocwalk.jqbox._
import ocwalk.mvc.{Controller, Model}
import ocwalk.style._
import ocwalk.util.global.GlobalContext
import ocwalk.util.http._
import ocwalk.util.logging.Logging
import ocwalk.util.{animation, fonts, spring, tilesets}
import org.scalajs.dom._

import scala.concurrent.Future

/** Starts the UI application */
//noinspection TypeAnnotation
object app extends App with GlobalContext with Logging {
  override protected def logKey: String = "app"

  config.setGlobalReader(JsReader)
  implicit val conf: OcwalkConfig = ocwalk.conf.Config

  history.location.pathname match {
    case discord if discord.startsWith("/discord") =>
      queryParameter("code") match {
        case Some(code) => loginDiscord(code)
        case None =>
          log.warn("login error, redirecting to [/]")
          redirect("/")
      }
    case path =>
      startOcwalk(path)
  }

  /** Launches the application */
  def startOcwalk(path: String): Unit = {
    val model = Model()
    implicit val controller = Controller(model, conf)
    val future = for {
      _ <- fonts.load(roboto :: robotoSlab :: materialIcons :: Nil)
      _ <- tilesets.load(tileset :: ImageStyle.EmptyTileset :: Nil)
      _ = {
        val refreshScreenSize = () => controller.setScreenSize(window.innerWidth.toInt xy window.innerHeight.toInt)
        window.addEventListener("resize", (_: Event) => refreshScreenSize(), useCapture = false)
        refreshScreenSize()
        scaleToScreen(controller)
      }
      _ <- spring.load()
      _ <- animation.load()
      _ <- controller.start(path)
    } yield ()
    future.whenFailed(up => log.error("failed to build ui", up))
  }

  /** Cleans up location after discord oauth2 flow */
  def loginDiscord(code: String)(implicit config: OcwalkConfig): Unit = for {
    user <- Future.successful(None) // post[LoginDiscord, User]("/api/discord", LoginDiscord(code))
    _ = log.info(s"logged in as [$user]")
    _ = redirectSilent("/")
    _ = startOcwalk("/")
  } yield user

}