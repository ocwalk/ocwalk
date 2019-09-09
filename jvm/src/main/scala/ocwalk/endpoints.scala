package ocwalk

import akka.http.scaladsl.server.Directives._
import ocwalk.configs.OcwalkConfig
import ocwalk.protocol.LoginDiscord
import ocwalk.sessions.SessionManagerRef
import ocwalk.util.akkautil._

//noinspection TypeAnnotation
object endpoints {
  val `GET /api/health` = get & path("api" / "health")

  def `GET /api/config`(implicit manager: SessionManagerRef, config: OcwalkConfig) = get & path("api" / "config") & adminSession()

  def `POST /api/discord`(implicit manager: SessionManagerRef, config: OcwalkConfig) = post & path("api" / "discord") & session() & entity(as[LoginDiscord])

  def `GET /api/user`(implicit manager: SessionManagerRef, config: OcwalkConfig) = get & path("api" / "user") & session()

  def `POST /api/signout`(implicit manager: SessionManagerRef, config: OcwalkConfig) = post & path("api" / "signout") & session()
}