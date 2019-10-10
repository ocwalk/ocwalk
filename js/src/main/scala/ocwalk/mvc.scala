package ocwalk

import ocwalk.common._
import ocwalk.conf.OcwalkConfig
import ocwalk.model.Note
import ocwalk.util.global.GlobalContext
import ocwalk.util.http
import ocwalk.util.logging.Logging
import ocwalk.util.timer.{Animator, Timer}

import scala.concurrent.Future

object mvc {

  /** Defines controller with common functionality */
  case class Controller(model: Model, config: OcwalkConfig) extends Logging with GlobalContext {
    override protected def logKey: String = "controller"

    val timer = new Timer()
    val animator = new Animator()

    /** Updates the rendering screen size */
    def setScreenSize(size: Vec2i): Unit = model.screen.write(size)

    /** Updates the global mouse position on the screen */
    def setMousePosition(mouse: Vec2d): Unit = model.mouse.write(mouse)

    /** Updates the current microphone volume level */
    def setInputVolume(level: Double): Double = model.inputVolume.write(level)

    /** Updates the detected note */
    def setDetection(detection: Option[Detection]): Unit = model.detection.write(detection)

    /** Launches the controller at a given application path */
    def start(path: String): Future[Unit] = Future {
      log.info(s"starting at path [$path]")
      router.start(this)
      pages.pages.start(this)
      detection.bind(this)
      timer.start(60, () => model.tick.write(model.tick() + 1))
      animator.start(() => model.frame.write(model.frame() + 1))
      log.info(s"started")

      model.page /> {
        case page => http.updateTitle(s"OCWALK - ${page.title}")
      }
      model.page /> {
        case page if page.detection => detection.start(this)
        case other => detection.stop(this)
      }
      log.info("bound")
    }

    /** Starts the pitch detection on user request */
    def startDetection(): Unit = {
      detection.start(this)
    }

    /** Redirects to kickstarter page */
    def showKickstarter(): Unit = {
      http.redirectFull("https://www.kickstarter.com/projects/owispyo/ocwalk")
    }

    /** Redirects to join discord page */
    def showDiscord(): Unit = {
      http.redirectFull("https://discord.gg/FJ7r34W")
    }

    /** Redirect to given page within ocwalk */
    def showPage(page: Page): Unit = {
      model.page.write(page)
    }
  }

  /** Defines model with common fields
    *
    * @param tick        the current update tick
    * @param frame       the current rendering frame
    * @param screen      the current screen size
    * @param scale       the current screen scale
    * @param mouse       current mouse coordinates
    * @param page        currently displayed ocwalk page
    * @param inputVolume the current volume level from microphone
    * @param detector    transition indicating whether or not the pitch detection is loaded
    * @param detection   the description of detected note
    */
  case class Model(tick: Writeable[Long] = Data(0),
                   frame: Writeable[Long] = Data(0),
                   screen: Writeable[Vec2i] = Data(0 xy 0),
                   scale: Writeable[Double] = Data(1.0),
                   mouse: Writeable[Vec2d] = Data(Vec2d.Zero),
                   page: Writeable[Page] = LazyData(router.parsePage),
                   inputVolume: Writeable[Double] = LazyData(0.0),
                   detector: Writeable[Transition[Unit]] = Data(Transition.Missing()),
                   detection: Writeable[Option[Detection]] = LazyData(None))

  /** The current application page */
  sealed trait Page {
    /** Returns the subtitle for the page */
    def title: String

    /** Returns true if the page required microphone input */
    def detection: Boolean = false
  }

  /** The starting page of the application */
  case class HomePage(foo: Option[String]) extends Page {
    override def title: String = "Home"
  }

  /** The page displaying sound spectrum from microphone */
  case class PitchPage() extends Page {
    override def title: String = "Pitch detection"

    override def detection: Boolean = true
  }

  /** The page containing a specific ocwalk project */
  case class ProjectPage(id: String, foo: Option[String], bars: List[Int]) extends Page {
    override def title: String = "Project"
  }

  /** Currently detected note from the spectrum
    *
    * @param note      the closest note to detected pitch
    * @param frequency the original detected pitch
    * @param cents     the cents distance between closest note and detected pitch
    */
  case class Detection(note: Note,
                       frequency: Double,
                       cents: Double)

}