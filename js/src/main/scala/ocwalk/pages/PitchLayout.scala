package ocwalk.pages

import lib.facade.pixi.Graphics
import lib.pixi._
import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._
import ocwalk.util.global.GlobalContext

/** Pitch page demo layout */
object PitchLayout extends JqBoxLayout[PitchPage] with GlobalContext {
  private implicit val listenerId: ListenerId = ListenerId()

  override def open(controller: Controller): Box = {
    val box = region(pitchPageId)
    val noteText = text(pitchNoteId)
    val pitchText = text().addClass(pitchParamClass)
    val volumeText = text().addClass(pitchParamClass)
    val errorText = text().addClass(pitchParamClass)
    val spectrum = drawBox(pitchSpectrumId)
    box.sub(
      vbox().sub(
        spectrum,
        hbox().fillBoth().sub(
          vbox().fillBoth().sub(
            pitchText,
            volumeText,
            errorText
          ),
          noteText.fillBoth()
        )
      )
    )

    val app = create(spectrum)
    val graphics = new Graphics
    app.stage.addChild(graphics)

    controller.model.frame /> { case _ =>
      graphics.clear()
      val pad = 10
      val size = spectrum.layout.relBounds().size
      val volume = controller.model.inputVolume()
      graphics.fillRect(size = (volume * (size.x - pad * 2)) xy 10, position = pad xy pad)
    }

    controller.model.inputVolume /> { case volume =>
      volumeText.textValue(s"Volume: ${(volume * 100).pretty(0)}%")
    }

    controller.model.detection /> {
      case Some(Detection(note, pitch, error)) =>
        noteText.textValue(note.label)
        pitchText.textValue(s"Pitch: ${pitch.pretty()}")
        errorText.textValue(s"Error: ${error.pretty(digits = 0)}¢")
      case None =>
        noteText.textValue("N/A")
        pitchText.textValue("Pitch: N/A")
        errorText.textValue(s"Error: N/A")
    }
    box
  }

  override def close(controller: Controller): Unit = {
    controller.model.detection.forget()
  }
}