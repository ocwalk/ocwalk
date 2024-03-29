package lib

import lib.facade.ffo.FontFaceObserver
import ocwalk.box.Font
import ocwalk.common._
import ocwalk.util.global.GlobalContext

import scala.concurrent.Future

/** Scala api for FontFaceObserver.
  * https://github.com/bramstein/fontfaceobserver */
object ffo extends GlobalContext {
  /** Waits until the webfont is loaded on the page */
  def load(font: Font): Future[Unit] = {
    new FontFaceObserver(font.family).load().toFuture.clear
  }
}