package ocwalk.audio

import ocwalk.audio.usermedia.AudioStream
import org.scalajs.dom.raw.AudioContext

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.{Float32Array, Uint8Array}

/** Node-style stream for getUserMedia.
  *
  * https://www.npmjs.com/package/microphone-stream
  */
@js.native
@JSGlobal("micstream")
class micstream extends js.Object {
  val context: AudioContext = js.native

  def setStream(steam: AudioStream): Unit = js.native

  def on(event: String, code: js.Function1[Uint8Array, Unit]): Unit = js.native
}

object micstream extends js.Object {
  def toRaw(data: Uint8Array): Float32Array = js.native
}