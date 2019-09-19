package ocwalk.uri

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("URI")
object URI extends js.Object {
  /** Parses the passed query string into an object. Returns object {propertyName: propertyValue} */
  def parseQuery(query: String): js.Dictionary[Any] = js.native
}