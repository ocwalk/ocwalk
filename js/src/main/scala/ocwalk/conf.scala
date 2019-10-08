package ocwalk

import ocwalk.config._
import ocwalk.format._
import ocwalk.util.http
import ocwalk.util.logging.Logging

object conf extends Logging {
  override protected def logKey: String = "config"

  /** Reads the config keys from java runtime environment */
  object JsReader extends ConfigReader {
    private val params = http.queryParameters

    override def get(path: Path): Option[String] = {
      params.get(path.stringify).flatMap(v => v.headOption)
    }
  }

  /** General configuration for all projects
    *
    * @param server              the protocol and host part for server uris
    * @param client              the protocol and host part for client uris
    * @param discordLogin        the redirect url for discord login
    * @param logs                the logging levels configuration
    * @param pitchModelPath      the root path to machine learning model files
    * @param pitchErrorThreshold the number of cents to be a default threshold for perfect pitches
    * @param pitchDotCount       the number of dots to display on pitch page
    * @param pitchDotSize       the size of dots to display on pitch page
    */
  case class OcwalkConfig(server: String,
                          client: String,
                          discordLogin: String,
                          logs: LogConfig,
                          pitchModelPath: String,
                          pitchErrorThreshold: Double,
                          pitchDotCount: Int,
                          pitchDotSize: Int)

  /** Configures logging on different levels */
  case class LogConfig(wire: Boolean,
                       debug: Boolean,
                       info: Boolean,
                       warnings: Boolean,
                       errors: Boolean)

  val DefaultConfig = OcwalkConfig(
    server = "http://127.0.0.1:8081",
    client = http.hostPortString,
    discordLogin = s"https://discordapp.com/api/oauth2/authorize?client_id=583316882002673683&redirect_uri=${http.hostPortString}/discord&response_type=code&scope=identify",
    logs = LogConfig(wire = false, debug = false, info = true, warnings = true, errors = true),
    pitchModelPath = "/crepe/",
    pitchErrorThreshold = 20.0,
    pitchDotCount = 100,
    pitchDotSize = 5
  )

  implicit val logConfigFormat: CF[LogConfig] = format5(LogConfig)
  implicit val ocwalkConfigFormat: CF[OcwalkConfig] = format8(OcwalkConfig)

  lazy val Config: OcwalkConfig = configureNamespace("ocwalk", Some(DefaultConfig))

}