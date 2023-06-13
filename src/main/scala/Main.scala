import ackcord._
import ackcord.data.GuildId
import ackcord.interactions.InteractionsRegistrar
import ackcord.requests.CreateMessage
import ackcord.syntax._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val config = AllConfig.config match {
    case Right(value) => value
    case Left(value) => sys.error(s"Failed to parse config - $value")
  }

  val token    = config.discordToken
  val guildId  = config.guildId
  val clientId = config.clientId

  val clientSettings = ClientSettings(token)
  //import clientSettings.executionContext

  val client = Await.result(clientSettings.createClient(), Duration.Inf)
  import client.executionContext

  client.onEventSideEffectsIgnore {
    case _: APIMessage.Ready => println("Now ready")
  }

  val myListeners = new MyListeners(client)
  client.registerListener(myListeners.createListeners)

  client.login()
}
