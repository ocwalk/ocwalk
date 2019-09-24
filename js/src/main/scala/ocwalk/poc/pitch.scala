package ocwalk.poc

import ocwalk.box.{Box, _}
import ocwalk.common._
import ocwalk.jqbox._
import ocwalk.mvc.{Controller, Detection}
import ocwalk.style._
import ocwalk.util.global.GlobalContext
import ocwalk.util.logging.Logging
import ocwalk.wad.wad

import scala.util.Try

object pitch extends GlobalContext with Logging {
  override protected def logKey: String = "poc.spectrum"

  /** Builds a spectrum POC */
  def init(controller: Controller, box: Box): Unit = {
    val noteText = text(noteId)
    val pitchText = text(pitchId)
    box.sub(
      vbox().sub(noteText, pitchText)
    )

    controller.model.detection /> {
      case Some(Detection(note, pitch, error)) =>
        noteText.textValue(note.label)
        pitchText.textValue(s"Pitch: ${pitch.pretty(digits = 0)}")
      case None =>
        noteText.textValue("N/A")
        pitchText.textValue("Pitch: N/A")
    }

    val voice = wad.apply(wad.Config(source = wad.Mic))
    val tuner = wad.poly()
    tuner.setVolume(0)
    tuner.add(voice)
    tuner.updatePitch()

    (controller.model.tick && controller.model.micEnabled) /> {
      case (tick, None) if wad.micConsent => controller.setMicEnabled(true)
    }

    controller.model.micEnabled /> {
      case Some(true) => tuner.play()
    }

    controller.model.tick /> { case tick =>
      val detection = for {
        _ <- Some()
        pitch <- tuner.pitch.toOption
        noteLabel <- tuner.noteName.toOption
        note <- Try(ocwalk.model.parseNote(noteLabel)).toOption
      } yield Detection(note, pitch, 0.0)
      controller.setDetection(detection)
    }
  }
}