package ocwalk.audio

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSGlobal

/** Light-weight Ponyfill/Polyfill for navigator.mediaDevices.getUserMedia.
  * Wraps the older callback-based navigator.getUserMedia when necessary.
  *
  * https://www.npmjs.com/package/get-user-media-promise
  */
@js.native
@JSGlobal("usermedia")
object usermedia extends js.Object {
  def apply(config: js.Dynamic = js.native): Promise[AudioStream] = js.native

  trait AudioStream extends js.Any

}