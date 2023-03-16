import ackcord._
import akka.NotUsed

class Listeners(client: DiscordClient) extends EventsController(client.requests) {

  val createListeners =  TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
      val startMessage = m.event.message

      if (startMessage.content.startsWith("start listen ")) {
        val inChannel  = m.channel.id
        val identifier = startMessage.content.replaceFirst("start listen ", "")

        val listener = client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
          if (m.channel.id == inChannel) {
            println(s"$identifier: ${m.event.message.channelId}")
          }
        })

        //We need lazy and an explicit type here to make Scala happy
        lazy val stopper: EventRegistration[NotUsed] =
          client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
            if (m.channel.id == inChannel && m.event.message.content == "stop listen " + identifier) {
              listener.stop()
              stopper.stop()
            }
          })

        //Initialize stopper
        stopper
      }
    }
}