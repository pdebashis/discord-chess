import ackcord._
import ackcord.data._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val config = Config.config match {
    case Right(value) => value
    case Left(value) => sys.error(s"Failed to parse config - $value")
  }

  val token = config.discordToken

  val clientSettings = ClientSettings(token)

  val client = Await.result(clientSettings.createClient(), Duration.Inf)

  client.onEventSideEffectsIgnore {
    case _: APIMessage.Ready => println("Now ready")
  }

  client.login()
}
