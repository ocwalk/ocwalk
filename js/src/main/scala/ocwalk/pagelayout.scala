package ocwalk

import ocwalk.box._
import ocwalk.common._
import ocwalk.mvc._
import ocwalk.router.Route
import ocwalk.style._

object pagelayout {

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

    override def open(controller: Controller): Box = {
      val box = region(pitchPageId)
      val noteText = text(noteId)
      val pitchText = text(pitchId)
      val centsText = text(centsId)
      val volumeText = text(inputVolumeId)
      box.sub(
        vbox().sub(noteText, pitchText, centsText)
      )

      controller.model.inputVolume /> {
        case level => volumeText.textValue(s"Volume: ${(level * 100).pretty(digits = 0)}%")
      }

      controller.model.detection /> {
        case Some(Detection(note, pitch, cents)) =>
          noteText.textValue(note.label)
          pitchText.textValue(s"Pitch: ${pitch.pretty(digits = 0)}")
          centsText.textValue(s"Error: ${cents.pretty(digits = 0)}¢")
        case None =>
          noteText.textValue("N/A")
          pitchText.textValue("Pitch: N/A")
          centsText.textValue("Error: N/A")
      }

      detection.start(controller)
      box
    }

    override def close(controller: Controller): Unit = {
      controller.model.detection.forget()
      detection.stop(controller)
    }
  }

}