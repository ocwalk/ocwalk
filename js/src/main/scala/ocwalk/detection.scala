package ocwalk

import lib.facade.wad.{Poly, Wad}
import lib.wad
import ocwalk.common.{Data, ListenerId, Writeable, _}
import ocwalk.mvc.{Controller, Detection}

import scala.scalajs.js.typedarray.Uint8Array
import scala.util.Try

/** Pitch detection algorithm */
object detection {
  private implicit val listenerId: ListenerId = ListenerId()
  private val voice: Writeable[Option[Wad]] = Data(None)
  private val tuner: Writeable[Option[Poly]] = Data(None)

  /** Starts the pitch detection and requests microphone access */
  def start(controller: Controller): Unit = if (voice().isEmpty) {
    val mic = wad.apply(wad.Config(source = wad.Mic))
    //val analyser = mic.audioContext.createAnalyser()
    //val spectrum = new Uint8Array(analyser.frequencyBinCount)
    voice.write(Some(mic))

    voice /> {
      case Some(v) =>
        val tuner = wad.poly()
        tuner.setVolume(0)
        tuner.add(v)
        tuner.updatePitch()
        this.tuner.write(Some(tuner))
    }

    (controller.model.tick && controller.model.micEnabled) /> {
      case (tick, None) if wad.micConsent => controller.setMicEnabled(true)
    }

    (controller.model.micEnabled && tuner) /> {
      case (Some(true), Some(t)) => t.play()
    }

    (controller.model.tick && tuner) /> {
      case (tick, Some(t)) =>
        //analyser.getByteFrequencyData(spectrum)
        //val volume = calculateVolume(spectrum)
        //controller.setInputVolume(volume)

        val detection = for {
          _ <- Some()
          //if volume > controller.config.inputVolumeThreshold
          pitch <- t.pitch.toOption
          noteLabel <- t.noteName.toOption
          note <- Try(ocwalk.model.parseNote(noteLabel)).toOption
        } yield Detection(note, pitch, calculateCents(pitch, note.frequency))
        controller.setDetection(detection)
    }
  }

  /** Returns the distance between frequencies in cents http://hyperphysics.phy-astr.gsu.edu/hbase/Music/cents.html */
  def calculateCents(current: Double, target: Double): Double = 1200 * (current / target).log / 2.log


  /** Returns the accurate input volume from FFT spectrum values */
  def calculateVolume(spectrum: Uint8Array): Double = {
    val sum = spectrum.map(v => v * v).sum.toDouble
    Math.sqrt(sum / spectrum.length)
  }

  /** Stops the pitch detection */
  def stop(controller: Controller): Unit = {
    tuner().foreach { w =>
      w.stopUpdatingPitch()
      w.stop()
      tuner.write(None)
    }
    voice().foreach { w =>
      w.stop()
      voice.write(None)
    }
    controller.model.tick.forget()
    controller.model.micEnabled.forget()
    voice.forget()
    tuner.forget()
  }
}