package ocwalk.pages

import lib.facade.pixi
import lib.facade.pixi.{Container, Graphics, Text}
import lib.pixi._
import ocwalk.box._
import ocwalk.ops._
import ocwalk.common._
import ocwalk.model.Note
import ocwalk.mvc._
import ocwalk.pages.pages.JqBoxLayout
import ocwalk.style._
import ocwalk.util.global.GlobalContext

/** Pitch page demo layout */
object PitchLayout extends JqBoxLayout[PitchPage] with GlobalContext {
  private implicit val listenerId: ListenerId = ListenerId()

  override def open(controller: Controller): Box = {
    val box = region(pitchPageId)
    val startButton = textButton().mutate(_.text.textValue("Start Detection"))
    startButton.onClick {
      controller.startDetection()
      startButton.layout.relEnabled.write(false)
    }
    controller.model.detector
      .whenLoading(startButton.text.textValue("Loading..."))
      .whenLoaded(_ => startButton.text.textValue("Successfully started!"))
      .whenFailed(_ => startButton.text.textValue("Failed to start"))
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
        ),
        startButton
      )
    )

    val app = create(spectrum)
    val w = spectrum.layout.fixedW().getOrElse(0.0)
    val h = spectrum.layout.fixedH().getOrElse(0.0)

    val dotContainer = new Container
    val dotLeftPad = 33
    val dotRightPad = 7
    val dotGap = (w - dotLeftPad - dotRightPad) / controller.config.pitchDotCount
    val dotSize = controller.config.pitchDotSize
    val dots = (0 until controller.config.pitchDotCount).map { index =>
      val dot = new Graphics
      dot.fillRoundRect(dotSize xy dotSize, dotSize / 2, Colors.PureWhite)
      dot.positionAt((dotLeftPad + index * dotGap) xy -100)
      dotContainer.addChild(dot)
    }

    val gridContainer = new Container
    val gridSize = h / 12
    val gridLeftWhitePad = 23
    val gridLeftBlackPad = 33
    val gridLabelPad = 7
    val gridRightPad = 7
    val gridLines = new Graphics
    gridLines.lineStyle(1, Colors.PureBlack.toDouble, 1, 0)
    val textStyle = new pixi.TextStyle
    textStyle.fontFamily = robotoSlab.family
    textStyle.fontSize = 16
    (0 until 12).foreach { index =>
      val note = Note(4, index)
      val label = note.label.dropRight(1)
      val y = h - gridSize * 0.5 - index * gridSize
      gridLines.moveTo(if (note.white) gridLeftWhitePad else gridLeftBlackPad, y)
      gridLines.lineTo(w - gridRightPad, y)
      val text = new Text
      text.text = label
      text.style = textStyle
      text.positionAt(gridLabelPad xy (y - textStyle.fontSize / 2))
      gridContainer.addChild(text)
    }
    gridContainer.addChild(gridLines)

    app.stage.addChild(gridContainer)
    app.stage.addChild(dotContainer)

    controller.model.frame /> { case _ =>
      // shift all dots to the left
      dots.dropRight(1).zipWithIndex.foreach { case (dot, index) =>
        val next = dots(index + 1)
        dot.position.y = next.position.y
        dot.tint = next.tint
      }
      // redraw the last dot to current value
      val last = dots.last
      controller.model.detection() match {
        case Some(detection) =>
          last.position.y = h - gridSize * 0.5 - (detection.note.offset + detection.cents / 100) * gridSize - dotSize / 2
          val color = if (detection.cents.abs < controller.config.pitchErrorThreshold) primaryColor else errorColor
          last.tint = color.toDouble
        case None =>
          last.position.y = -100
      }
    }

    controller.model.inputVolume /> { case volume =>
      volumeText.textValue(s"Volume: ${(volume * 100).pretty(0)}%")
    }

    controller.model.detection /> {
      case Some(Detection(note, pitch, error)) =>
        noteText.textValue(note.label)
        pitchText.textValue(s"Pitch: ${pitch.pretty()}")
        val sign = if (error < 0) "-" else "+"
        errorText.textValue(s"Error: $sign ${error.abs.pretty(digits = 0)}¢")
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