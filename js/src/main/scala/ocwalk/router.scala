package ocwalk

import ocwalk.format._
import ocwalk.mapping._
import ocwalk.mvc.{HomePage, Page, ProjectPage}
import ocwalk.util.http
import ocwalk.util.logging.Logging
import org.scalajs.dom.window

import scala.reflect.ClassTag
import scala.util.Try

/** Parses route from the current location */
object router extends Logging {
  override protected def logKey: String = "router"

  /** Describes a single routing to a page
    *
    * @param path   the routing path
    * @param format the mapper to appropriate page
    * @param tag    the class reference for the page
    * @tparam A the type of page
    */
  case class Route[A <: Page](path: String, format: MF[A], tag: Class[A])

  object Route {
    /** Creates route at given path */
    def apply[A <: Page](path: String, format: MF[A])(implicit tag: ClassTag[A]): Route[A] = {
      Route(path, format, tag.runtimeClass.asInstanceOf[Class[A]])
    }
  }

  /** A routing table for all application pages */
  val routes: List[Route[_]] = List(
    Route("/", format1(HomePage)),
    Route("/project/{id}", format3(ProjectPage))
  )


  /** Finds appropriate route by path matching and reads the page from path and query parameters */
  def parsePage: Page = {
    val path = window.location.pathname
    val query = window.location.search
    log.info(s"Routing [$path] with query [$query]")
    val defaultFormat = routes.head.format
    val parts = path.split("/").toList
    val (pathMapping: Mapping, format) = routes
      .map { route => route.path.split("/").toList -> route.format }
      .filter { case (routeParts, _) => routeParts.size == parts.size }
      .find { case (routeParts, _) =>
        routeParts.zip(parts).forall {
          case (routePart, part) if routePart.startsWith("{") && routePart.endsWith("}") => true
          case (routePart, part) => routePart == part
        }
      }
      .map { case (routeParts, routeFormat) =>
        val pathParams: Mapping = routeParts
          .zip(parts)
          .collect {
            case (rpart, part) if rpart.startsWith("{") && rpart.endsWith("}") => rpart.drop(1).dropRight(1) -> List(part)
          }
          .toMap
        pathParams -> routeFormat
      }
      .getOrElse(Map.empty -> defaultFormat)
    val fullMapping: Mapping = http.queryParameters ++ pathMapping
    val (page: Any, _) = Try(format.read(Nil, fullMapping)).getOrElse(defaultFormat.read(Nil, fullMapping))
    log.info(s"Routed to [$page]")
    page.asInstanceOf[Page]
  }

  /** Prints the route uri for a given page */
  def unparsePage(page: Page): String = {
    ""
  }

}