package ocwalk

import lib.facade.p5.AudioIn
import lib.p5
import ocwalk.common._
import ocwalk.mvc._

/** Pitch detection algorithm */
object detection {
  private implicit val listenerId: ListenerId = ListenerId()
  private val voiceData: Writeable[Option[AudioIn]] = Data(None)

  /** Binds the sound processing */
  def bind(controller: Controller): Unit = {
    voiceData /> {
      case Some(voice) => voice.start()
    }
    (voiceData && controller.model.tick) /> {
      case (Some(voice), tick) => controller.setInputVolume(voice.getLevel())
    }
  }

  /** Starts the pitch detection and requests microphone access */
  def start(controller: Controller): Unit = if (voiceData().isEmpty) {
    voiceData.write(Some(p5.audioIn()))
  }

  /** Returns the distance between frequencies in cents http://hyperphysics.phy-astr.gsu.edu/hbase/Music/cents.html */
  def calculateCents(current: Double, target: Double): Double = 1200 * (current / target).log / 2.log

  /** Stops the pitch detection */
  def stop(controller: Controller): Unit = {
    voiceData().foreach { voice =>
      voice.stop()
      voiceData.write(None)
    }
    controller.model.tick.forget()
    voiceData.forget()
  }
}