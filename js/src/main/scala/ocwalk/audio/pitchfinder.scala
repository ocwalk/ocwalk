package ocwalk.audio

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.Float32Array

/** A compilation of pitch detection algorithms for Javascript.
  *
  * https://github.com/peterkhayes/pitchfinder
  */
@js.native
@JSGlobal("pitchfinder")
object pitchfinder extends js.Object {

  /** Returns a reference to YIN algo */
  def YIN: js.Function1[js.Dynamic, Yin] = js.native

  /** Pitch detection algorithm */
  trait AudioAlgorithm extends js.Object {
    /** Returns the current pitch */
    def apply(data: Float32Array): Double = js.native
  }

  /** Implementation of YIN pitch detection */
  class Yin extends AudioAlgorithm

}