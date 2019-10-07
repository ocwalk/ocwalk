package lib

import lib.facade.p5.AudioIn
import ocwalk.util.global.GlobalContext
import org.scalajs.dom.raw.AudioContext

import scala.concurrent.{Future, Promise}

/** p5.js is a JavaScript library for creative coding, with a focus on making coding accessible and inclusive for artists, designers, educators, beginners, and anyone else!
  * p5.js is free and open-source because we believe software, and the tools to learn it, should be accessible to everyone.
  *
  * https://p5js.org/
  */
object p5 extends GlobalContext {
  /** Creates microphone input */
  def audioIn(): Future[AudioIn] = {
    val promise = Promise[AudioIn]()
    ec.execute(() => {
      val mic = new AudioIn({ () => promise.tryFailure(new IllegalStateException("Failed to create AudioIn")) })
      mic.start(
        successCallback = { () => promise.trySuccess(mic) },
        errorCallback = { () => promise.tryFailure(new IllegalStateException("Failed to start AudioIn")) }
      )
    })
    promise.future
  }

  /** Returns root audio context */
  def audioContext: AudioContext = lib.facade.p5.p5.getAudioContext()
}