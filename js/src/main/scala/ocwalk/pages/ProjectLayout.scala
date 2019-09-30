package ocwalk.pages

import ocwalk.box._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._

/** Page for a single ocwalk project */
object ProjectLayout extends JqBoxLayout[ProjectPage] {
  override def open(controller: Controller): Box = {
    region()
  }
}