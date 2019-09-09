package ocwalk.general

import ocwalk.config.{ConfigReader, _}
import ocwalk.format.{Path, _}
import ocwalk.util.http
import ocwalk.util.logging.Logging

object config extends Logging {
  override protected def logKey: String = "general/config"

  /** Reads the config keys from java runtime environment */
  object JsReader extends ConfigReader {
    private val params = http.queryParameters

    override def get(path: Path): Option[String] = {
      params.get(path.stringify).flatMap(v => v.headOption)
    }
  }

  /** General configuration for all projects
    *
    * @param server the protocol and host part for server uris
    * @param client the protocol and host part for client uris
    */
  case class OcwalkConfig(server: String,
                           client: String,
                           discordLogin: String)

  val DefaultConfig = OcwalkConfig(
    server = "http://127.0.0.1:8081",
    client = http.hostPortString,
    discordLogin = s"https://discordapp.com/api/oauth2/authorize?client_id=583316882002673683&redirect_uri=${http.hostPortString}/discord&response_type=code&scope=identify"
  )

  implicit val generalConfigFormat: CF[OcwalkConfig] = format3(OcwalkConfig)

  lazy val Config: OcwalkConfig = configureNamespace("general", Some(DefaultConfig))

}