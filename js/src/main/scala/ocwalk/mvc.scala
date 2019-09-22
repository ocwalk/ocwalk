package ocwalk

import ocwalk.common._
import ocwalk.model.Note
import ocwalk.util.global.GlobalContext
import ocwalk.util.http._
import ocwalk.util.logging.Logging

import scala.concurrent.Future

object mvc {

  /** Defines controller with common functionality */
  case class Controller(model: Model) extends Logging with GlobalContext {
    override protected def logKey: String = "controller"

    /** Updates the rendering screen size */
    def setScreenSize(size: Vec2i): Unit = model.screen.write(size)

    /** Updates the global mouse position on the screen */
    def setMousePosition(mouse: Vec2d): Unit = model.mouse.write(mouse)

    /** Updates the detected pitch */
    def setPitch(pitch: Option[Double]): Unit = model.pitch.write(pitch)

    /** Updates the current spectrum */
    def setSpectrum(levels: List[Int]): Unit = model.spectrum.write(levels)

    /** Updates the detected note */
    def setDetection(detection: Option[Detection]): Unit = model.detection.write(detection)

    /** Launches the controller at a given application path */
    def start(path: String): Future[Unit] = Future {
      log.info(s"starting at path [$path]")
      updateTitle("OCWALK")
      bindRedirects()
    }

    /** Listens to page changes and saves the page in history */
    def bindRedirects(): Unit = {
      model.page /> {
        case page =>
      }
    }

    /** Redirects to kickstarter page */
    def showKickstarter(): Unit = {
      redirectFull("https://www.kickstarter.com/projects/owispyo/ocwalk")
    }


    /** Redirects to join discord page */
    def showDiscord(): Unit = {
      redirectFull("https://discord.gg/FJ7r34W")
    }
  }

  /** Defines model with common fields
    *
    * @param tick      the current update tick
    * @param screen    the current screen size
    * @param scale     the current screen scale
    * @param mouse     current mouse coordinates
    * @param page      currently displayed ocwalk page
    * @param pitch     current pitch detected from microphone input
    * @param spectrum  current levels on different sound frequencies
    * @param detection the description of detected note
    */
  case class Model(tick: Writeable[Long] = Data(0),
                   screen: Writeable[Vec2i] = Data(0 xy 0),
                   scale: Writeable[Double] = Data(1.0),
                   mouse: Writeable[Vec2d] = Data(Vec2d.Zero),
                   page: Writeable[Page] = Data(router.parsePage),
                   pitch: Writeable[Option[Double]] = Data(None),
                   spectrum: Writeable[List[Int]] = Data(Nil),
                   detection: Writeable[Option[Detection]] = Data(None))

  /** The current application page */
  sealed trait Page

  /** The starting page of the application */
  case class HomePage(foo: Option[String]) extends Page

  /** The page displaying sound spectrum from microphone */
  case class SpectrumPage() extends Page

  /** The page containing a specific ocwalk project */
  case class ProjectPage(id: String, foo: Option[String], bars: List[Int]) extends Page

  /** Currently detected note from the spectrum
    *
    * @param note      the closest note to detected pitch
    * @param frequency the original detected pitch
    * @param error     the relative error between closest note and detected pitch
    */
  case class Detection(note: Note,
                       frequency: Double,
                       error: Double)

}