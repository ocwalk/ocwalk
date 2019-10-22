package ocwalk.pages

import ocwalk.box._
import ocwalk.mvc._
import ocwalk.pages.HeaderLayout._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._

/** Project library page layout */
object LibraryLayout extends JqBoxLayout[LibraryPage] {
  private implicit val stylesheet: Styler = StyleSheet(
    under(libraryId).sub(
      isRegion |> (
        _.fillColor(whiteColor),
        )
    )
  )

  override def open(controller: Controller): Box = {
    region(libraryId).fillBoth.sub(
      vbox.fillBoth.sub(
        header(navHome :: navLibrary :: navShop :: Nil),
        container.fillY.sub(text.as("Library content")),
      )
    )
  }
}