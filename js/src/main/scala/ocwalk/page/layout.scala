package ocwalk.page

import lib.facade.wad.{Poly, Wad}
import lib.wad
import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.router.Route
import ocwalk.style._
import ocwalk.{jqbox, router}

import scala.util.Try

object layout {

  /** Wraps the page layout */
  trait PageLayout[A <: Page] {
    /** Returns the parent box for the page layout when page opens */
    def open(controller: Controller): Box

    /** Is called after the page is opened when inner page values change */
    def update(page: A): Unit = {}

    /** Inner method to deal with page types */
    def updateUntyped(page: Page): Unit = update(page.asInstanceOf[A])

    /** Is called when another page is opened */
    def close(controller: Controller): Unit = {}
  }

  /** Page layout for only jq boxes */
  trait JqBoxLayout[A <: Page] extends PageLayout[A] {
    implicit val context: BoxContext = jqbox.boxContext
  }

  /** Binds the layouts to controller model */
  def start(controller: Controller): Unit = {
    val currentRoute: Writeable[Option[Route[Page]]] = Data(None)
    (controller.model.page.map(p => router.findRoute(p)) && currentRoute) /> {
      case (route, None) =>
        currentRoute.write(Some(route))
        val parent = route.layout.open(controller)
        jqbox.boxContext.root.sub(parent)
        route.layout.updateUntyped(controller.model.page())
      case (route, Some(current)) =>
        if (!current.eq(route)) {
          currentRoute.write(Some(route))
          current.layout.close(controller)
          val parent = route.layout.open(controller)
          jqbox.boxContext.root.sub(parent)
        }
        route.layout.updateUntyped(controller.model.page())
    }
  }

  /** Home page layout */
  object HomeLayout extends JqBoxLayout[HomePage] {
    override def open(controller: Controller): Box = {
      region(dragonsId).sub(
        vbox().sub(
          text().textValue("Here be dragons..."),
          textButton()
            .mutate(_.text.textValue("Demo: Pitch Detection"))
            .onClick(controller.showPage(PitchPage())),
          textButton()
            .mutate(_.text.textValue("Visit Kickstarter (closed)"))
            .onClick(controller.showKickstarter()),
          textButton()
            .mutate(_.text.textValue("Join Discord"))
            .onClick(controller.showDiscord()),
        )
      )
    }
  }

  /** Page for a single ocwalk project */
  object ProjectLayout extends JqBoxLayout[ProjectPage] {
    override def open(controller: Controller): Box = {
      region()
    }
  }

  /** Pitch page demo layout */
  object PitchLayout extends JqBoxLayout[PitchPage] {
    private implicit val listenerId: ListenerId = ListenerId()
    private val voice: Writeable[Option[Wad]] = Data(None)
    private val tuner: Writeable[Option[Poly]] = Data(None)

    override def open(controller: Controller): Box = {
      val box = region(pitchPageId)
      val noteText = text(noteId)
      val pitchText = text(pitchId)
      box.sub(
        vbox().sub(noteText, pitchText)
      )

      voice /> {
        case Some(v) =>
          val tuner = wad.poly()
          tuner.setVolume(0)
          tuner.add(v)
          tuner.updatePitch()
          this.tuner.write(Some(tuner))
      }

      controller.model.detection /> {
        case Some(Detection(note, pitch, error)) =>
          noteText.textValue(note.label)
          pitchText.textValue(s"Pitch: ${pitch.pretty(digits = 0)}")
        case None =>
          noteText.textValue("N/A")
          pitchText.textValue("Pitch: N/A")
      }

      (controller.model.tick && controller.model.micEnabled) /> {
        case (tick, None) if wad.micConsent => controller.setMicEnabled(true)
      }

      (controller.model.micEnabled && tuner) /> {
        case (Some(true), Some(t)) => t.play()
      }

      (controller.model.tick && tuner) /> {
        case (tick, Some(t)) =>
          val detection = for {
            _ <- Some()
            pitch <- t.pitch.toOption
            noteLabel <- t.noteName.toOption
            note <- Try(ocwalk.model.parseNote(noteLabel)).toOption
          } yield Detection(note, pitch, 0.0)
          controller.setDetection(detection)
      }

      voice.write(Some(wad.apply(wad.Config(source = wad.Mic))))
      box
    }

    override def close(controller: Controller): Unit = {
      tuner().foreach { w =>
        w.stopUpdatingPitch()
        w.stop()
        tuner.write(None)
      }
      voice().foreach { w =>
        w.stop()
        voice.write(None)
      }
      controller.model.detection.forget()
      controller.model.tick.forget()
      controller.model.micEnabled.forget()
      voice.forget()
      tuner.forget()
    }
  }

}