package ocwalk

import ocwalk.common.{Vec2d, Vec2i, Writeable}

object mvc {

  /** Defines controller with common functionality */
  trait Controller {
    /** Returns the ref to app model */
    def model: Model

    /** Updates the rendering screen size */
    def setScreenSize(size: Vec2i): Unit

    /** Updates the global mouse position on the screen */
    def setMousePosition(mouse: Vec2d): Unit
  }

  /** Defines model with common fields */
  trait Model {
    /** Returns the current update tick */
    def tick: Writeable[Long]

    /** Returns the current screen size */
    def screen: Writeable[Vec2i]

    /** Returns the current screen scale */
    def scale: Writeable[Double]

    /** Returns current mouse coordinates */
    def mouse: Writeable[Vec2d]
  }

}