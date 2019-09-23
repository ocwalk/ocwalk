package ocwalk.wad.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/** Web Audio DAW. Use the Web Audio API for dynamic sound synthesis. It's like jQuery for your ears.
  * https://github.com/rserota/wad */
@js.native
@JSGlobal("Wad")
class Wad extends js.Object {
  def this(config: js.Dynamic = js.native) = this()
}