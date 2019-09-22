package ocwalk.poc

import ocwalk.audio.{micstream, pitchfinder, usermedia}
import ocwalk.box.Box
import ocwalk.common._
import ocwalk.model.Notes
import ocwalk.mvc.{Controller, Detection}
import ocwalk.util.global.GlobalContext
import ocwalk.util.logging.Logging

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array

object spectrum extends GlobalContext with Logging {
  override protected def logKey: String = "poc.spectrum"

  /** Builds a spectrum POC */
  def init(controller: Controller, box: Box): Unit = {
    val ms = new micstream()
    val algo = pitchfinder.YIN(js.Dynamic.literal(probabilityThreshold = 0.1))
    val analyser = ms.context.createAnalyser()
    analyser.fftSize = 2048
    val spectrum = new Uint8Array(analyser.frequencyBinCount)

    usermedia
      .apply(js.Dynamic.literal(audio = true, video = false))
      .toFuture
      .whenFailed(up => log.error("failed to connect to microphone", up))
      .whenSuccessful(stream => ms.setStream(stream))

    ms.on("data", { chunk =>
      val raw = micstream.toRaw(chunk)
      val pitch = Option(algo.apply(raw))
      analyser.getByteFrequencyData(spectrum)
      controller.setPitch(pitch)
      controller.setSpectrum(spectrum.toList.map(s => s.toInt))
    })

    controller.model.pitch /> { case pitchOpt =>
      val detection = for {
        pitch <- pitchOpt
        if pitch < 17640
        (closestNote, offsetLog) <- Notes
          .map(note => note -> (note.frequency.log - pitch.log))
          .minByOpt { case (note, offset) => offset.abs }
        adjacent <- if (offsetLog < 0) Notes.before(closestNote) else Notes.after(closestNote)
        error = offsetLog / (closestNote.frequency.log - adjacent.frequency.log).abs * 2
      } yield Detection(adjacent, pitch, error)
      controller.setDetection(detection)
    }

    controller.model.detection /> {
      case Some(Detection(note, pitch, error)) => log.info(s"detected [${note.label}] with pitch [$pitch] and error [$error]")
    }
  }
}