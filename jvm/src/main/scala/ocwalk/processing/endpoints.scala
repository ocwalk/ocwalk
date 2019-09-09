package ocwalk.processing

import akka.http.scaladsl.server.Directives._
import ocwalk.conf.OcwalkConfig
import ocwalk.processing.sessions.SessionManagerRef
import ocwalk.protocol.LoginDiscord
import ocwalk.util.akkautil._

//noinspection TypeAnnotation
object endpoints {
  val `GET /api/health` = get & path("api" / "health")

  def `POST /api/discord`(implicit manager: SessionManagerRef, config: OcwalkConfig) = post & path("api" / "discord") & session() & entity(as[LoginDiscord])

  def `GET /api/user`(implicit manager: SessionManagerRef, config: OcwalkConfig) = get & path("api" / "user") & session()

  def `POST /api/signout`(implicit manager: SessionManagerRef, config: OcwalkConfig) = post & path("api" / "signout") & session()
}