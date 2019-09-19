package ocwalk

import ocwalk.format._
import ocwalk.mapping._
import ocwalk.mvc.{HomePage, Page, ProjectPage}
import ocwalk.util.http
import ocwalk.util.logging.Logging

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
  case class Route[A <: Page](path: List[PathPart], format: MF[A], tag: Class[A])

  /** Part of the route path between slashed */
  trait PathPart

  /** A static path part that has to be exactly matched */
  case class ExactPathPart(value: String) extends PathPart

  /** A variable path part that expands route parameters */
  case class ParamPathPart(name: String) extends PathPart

  object Route {
    /** Creates route at given path */
    def apply[A <: Page](path: String, format: MF[A])(implicit tag: ClassTag[A]): Route[A] = {
      Route(split(path), format, tag.runtimeClass.asInstanceOf[Class[A]])
    }

    /** Splits the string path into route parts */
    def split(path: String): List[PathPart] = path.split("/").toList.map {
      case param if param.startsWith("{") && param.endsWith("}") => ParamPathPart(param.drop(1).dropRight(1))
      case exact => ExactPathPart(exact)
    }
  }

  /** A routing table for all application pages */
  val routes: List[Route[_]] = List(
    Route("/", format1(HomePage)),
    Route("/project/{id}", format3(ProjectPage))
  )

  /** The defaul route to fallback to on errors */
  val defaultRoute: Route[_] = routes.head

  /** Finds appropriate route by path matching and reads the page from path and query parameters */
  def parsePage: Page = {
    val path = http.pathString
    log.info(s"Routing [$path] with query [${http.queryString}]")
    val parts = path.split("/").toList
    val (pathMapping: Mapping, format) = routes
      .filter { route => route.path.size == parts.size }
      .find { route =>
        route.path.zip(parts).forall {
          case (ParamPathPart(_), _) => true
          case (ExactPathPart(exact), part) => exact == part
        }
      }
      .map { route =>
        val pathParams: Mapping = route.path
          .zip(parts)
          .collect { case (ParamPathPart(name), part) => name -> List(part) }
          .toMap
        pathParams -> route.format
      }
      .getOrElse(Map.empty -> defaultRoute.format)
    val fullMapping: Mapping = http.queryParameters ++ pathMapping
    val (page: Any, _) = Try(format.read(Nil, fullMapping)).getOrElse(defaultRoute.format.read(Nil, fullMapping))
    log.info(s"Routed to [$page]")
    page.asInstanceOf[Page]
  }

  /** Prints the route uri for a given page */
  def unparsePage(page: Page): String = {
    val route = routes
      .find(route => route.tag == page.getClass)
      .getOrElse(defaultRoute)
    val mapping = route.format.asInstanceOf[MF[Page]].append(Nil, page, Map.empty)
    ""
  }

}