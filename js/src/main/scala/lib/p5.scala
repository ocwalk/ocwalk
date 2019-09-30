package lib

import lib.facade.p5.AudioIn

/** p5.js is a JavaScript library for creative coding, with a focus on making coding accessible and inclusive for artists, designers, educators, beginners, and anyone else!
  * p5.js is free and open-source because we believe software, and the tools to learn it, should be accessible to everyone.
  *
  * https://p5js.org/
  */
object p5 {
  /** Creates microphone input */
  def audioIn(onerror: () => Unit = { () => }): AudioIn = new AudioIn()
}