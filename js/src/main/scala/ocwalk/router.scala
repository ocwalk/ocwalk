package ocwalk

import lib.history
import lib.uri._
import ocwalk.format._
import ocwalk.mapping._
import ocwalk.mvc._
import ocwalk.pages._
import ocwalk.pages.pages._
import ocwalk.util.http
import ocwalk.util.logging.Logging

import scala.reflect.ClassTag
import scala.util.Try

/** Parses route from the current location */
object router extends Logging {
  override protected def logKey: String = "router"

  /** A routing table for all application pages */
  val routes: List[Route[_]] = List(
    Route("/", format1(DragonsPage), DragonsLayout),
    Route("/project/{id}", format3(ProjectPage), ProjectLayout),
    Route("/pitch", format0(PitchPage), PitchLayout),
    Route("/home", format0(HomePage), HomeLayout),
    Route("/library", format0(LibraryPage), LibraryLayout),
  )

  /** The default route to fallback to on errors */
  val defaultRoute: Route[_] = routes.head

  /** Describes a single routing to a page
    *
    * @param path   the routing path
    * @param format the mapper to appropriate page
    * @param tag    the class reference for the page
    * @tparam A the type of page
    */
  case class Route[A <: Page](path: List[PathPart], format: MF[A], tag: Class[A], layout: PageLayout[A])

  /** Part of the route path between slashed */
  trait PathPart

  /** A static path part that has to be exactly matched */
  case class ExactPathPart(value: String) extends PathPart

  /** A variable path part that expands route parameters */
  case class ParamPathPart(name: String) extends PathPart

  object Route {
    /** Creates route at given path */
    def apply[A <: Page](path: String, format: MF[A], layout: PageLayout[A])(implicit tag: ClassTag[A]): Route[A] = {
      Route(split(path), format, tag.runtimeClass.asInstanceOf[Class[A]], layout)
    }

    /** Splits the string path into route parts */
    def split(path: String): List[PathPart] = path.split("/").toList.map {
      case param if param.startsWith("{") && param.endsWith("}") => ParamPathPart(param.drop(1).dropRight(1))
      case exact => ExactPathPart(exact)
    }
  }

  /** Finds appropriate route by path matching and reads the page from path and query parameters */
  def parsePage: Page = {
    val path = http.pathString
    log.info(s"routing [$path] with query [${http.queryString}]")
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
    val (anyPage: Any, _) = Try(format.read(Nil, fullMapping)).getOrElse(defaultRoute.format.read(Nil, fullMapping))
    val page = anyPage.asInstanceOf[Page]
    log.info(s"routed to [$page]")
    page
  }

  /** Returns a route for a given page */
  def findRoute(page: Page): Route[Page] = {
    routes
      .find(route => route.tag == page.getClass)
      .getOrElse(defaultRoute)
      .asInstanceOf[Route[Page]]
  }

  /** Prints the route path and query for a given page */
  def unparsePage(page: Page): String = {
    val route = findRoute(page)
    val mapping = route.format.append(Nil, page, Map.empty)
    val queryParams = mapping.filter { case (key, values) =>
      route.path.collectFirst { case ParamPathPart(name) if name == key => }.isEmpty
    }
    val path = route.path
      .map {
        case ExactPathPart(value) => value
        case ParamPathPart(name) => mapping(name).head
      }
      .mkString("/")
    uri(path).appendQuery(queryParams).toString
  }

  /** Listens to browser history events to switch to another page, redirects to new pages when model changes */
  def start(controller: Controller): Unit = {
    controller.model.page /> {
      case page =>
        val target = unparsePage(page)
        if (target != http.routeString) http.redirectSilent(target)
    }

    history.start(location => controller.showPage(parsePage))
  }

}