import ackcord._
import ackcord.syntax.TextChannelSyntax
import akka.NotUsed

import scala.util.Try

class Listeners(client: DiscordClient, CurrencyInstance: MyCurrency) extends EventsController(client.requests) {


  val createListeners =  TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
      val startMessage = m.event.message

      if (startMessage.content.startsWith("start")) {
        val inChannel  = m.channel.id
        val identifier = "Converting to INR"

        val listener = client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { implicit m =>
          if (m.channel.id == inChannel) {
            println(s"$identifier: ${m.event.message.content}")
            val amount = m.event.message.content
            if (Try(amount.toDouble).isSuccess) {
              val amountD = amount.toDouble
              println("Converting")
              val currencyToInr = CurrencyInstance.currency_rate
              val currencyToATM = CurrencyInstance.currency_atm
              val currencyToCC = CurrencyInstance.currency_cc
              val currencyToDollar = CurrencyInstance.currency_dollar
              val rupeeSymbol = "\u20B9"

              val currency_display_string = s"$rupeeSymbol `${currencyToInr * amountD}` rupees\n" +
                s"$rupeeSymbol `${currencyToATM * amountD}` (Cash)\n$rupeeSymbol `${currencyToCC * amountD}` (Card)\n" +
              s"`$$ ${currencyToDollar * amountD}`"

              client.requestsHelper.run(m.channel.sendMessage(content = currency_display_string))
            }
          }
        })

        //We need lazy and an explicit type here to make Scala happy
        lazy val stopper: EventRegistration[NotUsed] =
          client.registerListener(TextChannelEvent.on[APIMessage.MessageCreate].withSideEffects { m =>
            if (m.channel.id == inChannel && m.event.message.content == "stop") {
              listener.stop()
              stopper.stop()
            }
          })

        //Initialize stopper
        stopper
      }
    }

}