package ocwalk

import ocwalk.box.BoxClass.{Disabled, Hover}
import ocwalk.box.ImageStyle._
import ocwalk.box._
import ocwalk.common._

/** Ocwalk application style */
//noinspection TypeAnnotation
object style {
  val primaryColor = Colors.Green500
  val errorColor = Colors.Red500

  val tileset = Tileset("/tileset/ocwalk")

  val logo = tileset.source("/image/test-1.png")
  val logoBlackFull = logo.ref(color = _ => Colors.PureBlack)
  val logoPrimary32 = logo.ref(size = 32 xy 32, color = _ => primaryColor)
  val logoWhite32 = logo.ref(size = 32 xy 32, color = _ => Colors.PureWhite)
  val logoWhite64 = logo.ref(size = 64 xy 64, color = _ => Colors.PureWhite)
  val logoBlack64 = logo.ref(size = 64 xy 64, color = _ => Colors.PureBlack)

  val test = tileset.source("/image/test-2.png")
  val testTintFull = test.ref(color = _.tint(Colors.Blue500, 0.5))
  val test32 = test.ref(size = 32 xy 32)
  val testRed64 = test.ref(size = 64 xy 64, color = _ => Colors.Red500)

  val roboto = Font("Roboto")
  val robotoSlab = Font("Roboto Slab")
  val materialIcons = Font("Material Icons")

  val dragonsId = BoxId()

  val pitchPageId = BoxId()
  val pitchNoteId = BoxId()
  val pitchParamClass = BoxClass()
  val pitchSpectrumId = BoxId()

  implicit val styler: Styler = StyleSheet(
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
        _.fillX(),
        _.fillColor(Colors.Green500),
        _.pad(20.0 xy 20.0),
        _.cursor(Cursors.Auto),
      ),
      isTextButton && Hover |> (
        _.fillColor(Colors.Green500.lighter),
        _.cursor(Cursors.Pointer)
      ),
    ),

    under(pitchPageId).sub(
      isRegion |> (
        _.fillColor(Colors.PureWhite)
        ),
      isText |> (
        _.textFont(robotoSlab),
        _.textColor(Colors.PureBlack)
      ),
      isVBox |> (
        _.spacingY(10.0)
        ),
      isHBox |> (
        _.spacingX(10.0),
        _.pad(10.0 xy 0.0)
      ),
      isText && pitchNoteId |> (
        _.textSize(72.0)
        ),
      isText && pitchParamClass |> (
        _.textSize(24.0),
        _.align(Vec2d.Left)
      ),
      isContainer && pitchSpectrumId |> (
        _.fixedW(320),
        _.fixedH(348)
      ),
      isText && hasAbsParent(isTextButton) |> (
        _.textFont(robotoSlab),
        _.textColor(Colors.PureWhite),
        _.textSize(24.0),
      ),
      isTextButton |> (
        _.fillX(),
        _.fillColor(Colors.Green500),
        _.pad(10.0 xy 10.0),
        _.cursor(Cursors.Auto),
      ),
      isTextButton && Hover |> (
        _.fillColor(Colors.Green500.lighter),
        _.cursor(Cursors.Pointer)
      ),
      isTextButton && Disabled |> (
        _.fillColor(Colors.Green500.darker),
        _.cursor(Cursors.Auto)
      )
    )
  )
}