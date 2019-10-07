package ocwalk

import lib.facade.ml5.PitchDetection
import lib.facade.p5.AudioIn
import lib.ml5._
import lib.{ml5, p5}
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.util.global.GlobalContext
import ocwalk.util.logging.Logging

/** Pitch detection algorithm */
object detection extends GlobalContext with Logging {
  override protected def logKey: String = "detection"

  private implicit val listenerId: ListenerId = ListenerId()
  private val voiceData: Writeable[Option[AudioIn]] = Data(None)
  private val pitchData: Writeable[Option[PitchDetection]] = Data(None)

  /** Binds the sound processing */
  def bind(controller: Controller): Unit = {
    (voiceData && controller.model.tick) /> {
      case (Some(voice), tick) => controller.setInputVolume(voice.getLevel())
    }
  }

  /** Starts the pitch detection and requests microphone access */
  def start(controller: Controller): Unit = if (voiceData().isEmpty) {
    (for {
      _ <- UnitFuture
      _ = log.info("connecting microphone")
      voice <- p5.audioIn()
      _ = log.info("loading pitch detection model")
      pitch <- ml5.pitchDetection(controller.config.pitchModelPath, voice)
      _ = log.info("starting pitch detection")
      _ = pitch.register({ frequency =>
        val result = Detection(model.Notes.head, frequency.getOrElse(-1.0), -1.0)
        controller.setDetection(Some(result))
      })
    } yield {
      voiceData.write(Some(voice))
      pitchData.write(Some(pitch))
      log.info("started")
    }).whenFailed(up => log.error("failed to start detection", up))
  }

  /** Returns the distance between frequencies in cents http://hyperphysics.phy-astr.gsu.edu/hbase/Music/cents.html */
  def calculateCents(current: Double, target: Double): Double = 1200 * (current / target).log / 2.log

  /** Stops the pitch detection */
  def stop(controller: Controller): Unit = {
    voiceData().foreach { voice =>
      voice.stop()
      voiceData.write(None)
    }
    pitchData().foreach { pitch =>
      pitchData.write(None)
    }
    controller.model.tick.forget()
    voiceData.forget()
    pitchData.forget()
  }
}