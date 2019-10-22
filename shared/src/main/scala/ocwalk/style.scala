package ocwalk

import ocwalk.box.ImageStyle._
import ocwalk.box._
import ocwalk.common._

/** Ocwalk application style */
//noinspection TypeAnnotation
object style {
  /** COMMON */
  val primaryColor = Colors.Green500
  val errorColor = Colors.Red500
  val whiteColor = Colors.PureWhite
  val blackColor = Colors.PureBlack

  val tileset = Tileset("/tileset/ocwalk")

  val logo = tileset.source("/image/test-1.png")
  val logoBlackFull = logo.ref(color = _ => blackColor)
  val logoPrimary32 = logo.ref(size = 32 xy 32, color = _ => primaryColor)
  val logoWhite32 = logo.ref(size = 32 xy 32, color = _ => whiteColor)
  val logoWhite64 = logo.ref(size = 64 xy 64, color = _ => whiteColor)
  val logoBlack64 = logo.ref(size = 64 xy 64, color = _ => blackColor)

  val test = tileset.source("/image/test-2.png")
  val testTintFull = test.ref(color = _.tint(Colors.Blue500, 0.5))
  val test32 = test.ref(size = 32 xy 32)
  val testRed64 = test.ref(size = 64 xy 64, color = _ => errorColor)

  val roboto = Font("Roboto")
  val robotoSlab = Font("Roboto Slab")
  val materialIcons = Font("Material Icons")

  /** PARTS */
  val headerId = BoxId()
  val navId = BoxId()
  val navButtonClass = BoxClass()

  /** DRAGONS */
  val dragonsId = BoxId()

  /** PITCH DETECTION DEMO */
  val pitchPageId = BoxId()
  val pitchNoteId = BoxId()
  val pitchParamClass = BoxClass()
  val pitchSpectrumId = BoxId()

  /** HOME */
  val homeId = BoxId()

  /** Library */
  val libraryId = BoxId()
}