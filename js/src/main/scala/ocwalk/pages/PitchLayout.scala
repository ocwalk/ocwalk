package ocwalk.pages

import lib.facade.pixi.{Graphics, Sprite, Texture}
import lib.pixi
import lib.pixi._
import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._
import ocwalk.util.global.GlobalContext
import org.scalajs.dom

/** Pitch page demo layout */
object PitchLayout extends JqBoxLayout[PitchPage] with GlobalContext {
  private implicit val listenerId: ListenerId = ListenerId()

  override def open(controller: Controller): Box = {
    val box = region(pitchPageId)
    val noteText = text(noteId)
    val pitchText = text(pitchId)
    val drawBox = canvas(pitchPixiId)
    box.sub(
      vbox().sub(noteText, pitchText, drawBox)
    )

    val app = create(drawBox)
    dom.window.console.log(app)
    dom.window.console.log(app.view)
    val graphics = new Graphics
    app.stage.addChild(graphics)
    val texture = Texture.from("/image/test-2.png")
    val sprite = new Sprite(texture)
    app.stage.addChild(sprite)

    controller.model.frame /> { case _ =>
      graphics.clear()
      val pad = 10
      val size = drawBox.layout.relBounds().size
      val volume = controller.model.inputVolume()
      graphics.fillRect(size = (volume * (size.x - pad * 2)) xy 10, position = pad xy pad)
    }

    controller.model.detection /> {
      case Some(Detection(note, pitch, _)) =>
        noteText.textValue(note.label)
        pitchText.textValue(s"Pitch: ${pitch.pretty(digits = 0)}")
      case None =>
        noteText.textValue("N/A")
        pitchText.textValue("Pitch: N/A")
    }
    box
  }

  override def close(controller: Controller): Unit = {
    controller.model.detection.forget()
  }
}