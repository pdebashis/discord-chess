import ackcord._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MyScalaBot extends App {
  val config = Config.config match {
    case Right(value) => value
    case Left(value) => sys.error(s"Failed to parse config - $value")
  }

  val token    = config.discordToken
  val guildId  = config.guildId

  val clientSettings = ClientSettings(token)
  //import clientSettings.executionContext

  val client = Await.result(clientSettings.createClient(), Duration.Inf)
  //import clients.executionContext

  client.onEventSideEffectsIgnore {
    case _: APIMessage.Ready => println("Now ready")
  }

  val myListeners = new Listeners(client)
  client.registerListener(myListeners.createListeners)



  client.login()
}
