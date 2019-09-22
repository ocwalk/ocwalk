package ocwalk

import ocwalk.box.BoxClass.Hover
import ocwalk.box.ImageStyle._
import ocwalk.box._
import ocwalk.common._

/** Ocwalk application style */
//noinspection TypeAnnotation
object style {
  val primary = Colors.Green500

  val tileset = Tileset("/tileset/ocwalk")

  val logo = tileset.source("/image/test-1.png")
  val logoBlackFull = logo.ref(color = _ => Colors.PureBlack)
  val logoPrimary32 = logo.ref(size = 32 xy 32, color = _ => primary)
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

  val spectrumId = BoxId()

  implicit val styler: Styler = StyleSheet(
    isRegion && dragonsId |> (
      _.fillColor(Colors.PureWhite)
      ),
    isVBox && hasAbsParent(dragonsId) |> (
      _.spacingY(20.0)
      ),
    isText && hasAbsParent(dragonsId) |> (
      _.textFont(robotoSlab),
      _.textColor(Colors.PureBlack),
      _.textSize(24.0)
    ),
    isBoxButton && hasAbsParent(dragonsId) |> (
      _.fillColor(Colors.Green500),
      _.pad(20.0 xy 20.0),
      _.cursor(Cursors.Auto)
    ),
    isBoxButton && Hover && hasAbsParent(dragonsId) |> (
      _.fillColor(Colors.Green500.lighter),
      _.cursor(Cursors.Pointer)
    ),
    isHBox && hasAbsParent(isBoxButton) |> (
      _.spacingX(10.0)
      ),
    isText && hasAbsParent(isBoxButton) |> (
      _.textFont(robotoSlab),
      _.textColor(Colors.PureWhite),
      _.textSize(24.0),
    ),
  )
}