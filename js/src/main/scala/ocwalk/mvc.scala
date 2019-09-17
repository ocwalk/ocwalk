package ocwalk

import ocwalk.common._
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

    /** Launches the controller at a given application path */
    def start(path: String): Future[Unit] = Future {
      log.info(s"starting at path [$path]")
      updateTitle("OCWALK")
    }

    /** Redirects to kickstarter page */
    def showKickstarter(): Unit = {
      redirectFull("https://www.kickstarter.com/projects/owispyo/ocwalk")
    }
  }

  /** Defines model with common fields
    *
    * @param tick   the current update tick
    * @param screen the current screen size
    * @param scale  the current screen scale
    * @param mouse  current mouse coordinates
    */
  case class Model(tick: Writeable[Long] = Data(0),
                   screen: Writeable[Vec2i] = Data(0 xy 0),
                   scale: Writeable[Double] = Data(1.0),
                   mouse: Writeable[Vec2d] = Data(Vec2d.Zero))

  sealed trait Route



}