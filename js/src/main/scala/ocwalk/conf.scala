package ocwalk

import ocwalk.config.{ConfigReader, _}
import ocwalk.format.{Path, _}
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
    * @param server               the protocol and host part for server uris
    * @param client               the protocol and host part for client uris
    * @param discordLogin         the redirect url for discord login
    * @param logs                 the logging levels configuration
    * @param inputVolumeThreshold the lowest microphone level when the detection will assume user is playing instrument
    * @param fftSmoothing         the amount of smoothing to be applied to fft calculations
    * @param fftBinCount          the sample buffer size for FFT analysis
    */
  case class OcwalkConfig(server: String,
                          client: String,
                          discordLogin: String,
                          logs: LogConfig,
                          inputVolumeThreshold: Double,
                          fftSmoothing: Double,
                          fftBinCount: Int)

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
    inputVolumeThreshold = 0.1,
    fftSmoothing = 0.8,
    fftBinCount = 2 * 1024
  )

  implicit val logConfigFormat: CF[LogConfig] = format5(LogConfig)
  implicit val ocwalkConfigFormat: CF[OcwalkConfig] = format7(OcwalkConfig)

  lazy val Config: OcwalkConfig = configureNamespace("ocwalk", Some(DefaultConfig))

}