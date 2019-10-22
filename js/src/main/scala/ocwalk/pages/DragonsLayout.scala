package ocwalk.pages

import ocwalk.box.BoxClass._
import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._

/** Dragons page layout */
object DragonsLayout extends JqBoxLayout[DragonsPage] {
  private implicit val stylesheet: Styler = StyleSheet(
    under(dragonsId).sub(
      isRegion |> (
        _.fillColor(Colors.PureWhite)
        ),
      isVBox |> (
        _.spacingY(20.0),
        ),
      isText |> (
        _.textFont(robotoSlab),
        _.textColor(Colors.PureBlack),
        _.textSize(24.0),
      ),
      isText && hasAbsParent(isTextButton) |> (
        _.textFont(robotoSlab),
        _.textColor(Colors.PureWhite),
        _.textSize(24.0),
      ),
      isTextButton |> (
        _.fillX,
        _.fillColor(Colors.Green500),
        _.pad(20.0 xy 20.0),
        _.cursor(Cursors.Auto),
      ),
      isTextButton && Hover |> (
        _.fillColor(Colors.Green500.lighter),
        _.cursor(Cursors.Pointer)
      ),
    )
  )

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