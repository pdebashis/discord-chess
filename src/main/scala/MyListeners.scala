import ackcord._
import ackcord.syntax.TextChannelSyntax
import akka.NotUsed

import scala.util.Try

class MyListeners(client: DiscordClient) extends EventsController(client.requests) {


  val createListeners =  TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
    val startMessage = m.event.message

    if (startMessage.content.startsWith("!duel")) {
      val inChannel  = m.channel.id

      val listener = client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { implicit m =>
        if (m.channel.id == inChannel) {
          client.requestsHelper.run(m.channel.sendMessage(content = "started a game"))
        }
      })

      //We need lazy and an explicit type here to make Scala happy
      lazy val stopper: EventRegistration[NotUsed] =
        client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
          if (m.channel.id == inChannel && m.event.message.content == "!resign") {
            listener.stop()
            stopper.stop()
          }
        })

      //Initialize stopper
      stopper
    }
  }

}