package ocwalk.pages

import ocwalk.box._
import ocwalk.mvc._
import ocwalk.pages.HeaderLayout._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._

/** Dragons page layout */
object HomeLayout extends JqBoxLayout[HomePage] {
  private implicit val stylesheet: Styler = StyleSheet(
    under(homeId).sub(
      isRegion |> (
        _.fillColor(whiteColor),
        )
    )
  )

  override def open(controller: Controller): Box = {
    region(homeId).fillBoth.sub(
      vbox.fillBoth.sub(
        header(navHome :: navLibrary :: navShop :: Nil),
        container.fillY.sub(text.as("Home content")),
      )
    )
  }
}