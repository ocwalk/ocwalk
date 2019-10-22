package ocwalk.pages

import ocwalk.box._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout

/** Page for a single ocwalk project */
object ProjectLayout extends JqBoxLayout[ProjectPage] {
  private implicit val stylesheet: Styler = StyleSheet()

  override def open(controller: Controller): Box = {
    region()
  }
}