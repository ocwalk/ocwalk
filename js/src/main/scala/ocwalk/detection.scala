package ocwalk

import lib.facade.ml5.PitchDetection
import lib.facade.p5.AudioIn
import lib.ml5._
import lib.{ml5, p5}
import ocwalk.common._
import ocwalk.model._
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
      _ = pitch.register({ frequencyOpt =>
        controller.setDetection(frequencyOpt.map { frequency =>
          val note = calculateNote(frequency)
          val error = calculateCents(frequency, note.frequency)
          Detection(note, frequency, error)
        })
      })
    } yield {
      voiceData.write(Some(voice))
      pitchData.write(Some(pitch))
      log.info("started")
    }).whenFailed(up => log.error("failed to start detection", up))
  }

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