package ocwalk.pages

import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._

/** Home page layout */
object HomeLayout extends JqBoxLayout[HomePage] {
  override def open(controller: Controller): Box = {
    region(dragonsId).sub(
      vbox().sub(
        text().textValue("Here be dragons..."),
        textButton()
          .mutate(_.text.textValue("Demo: Pitch Detection"))
          .onClick(controller.showPage(PitchPage())),
        textButton()
          .mutate(_.text.textValue("Visit Kickstarter (closed)"))
          .onClick(controller.showKickstarter()),
        textButton()
          .mutate(_.text.textValue("Join Discord"))
          .onClick(controller.showDiscord()),
      )
    )
  }
}