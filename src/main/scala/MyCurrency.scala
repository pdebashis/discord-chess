import ackcord.Requests
import ackcord.data.{EmbedField, OutgoingEmbed, OutgoingEmbedFooter}
import ackcord.interactions.commands._
import scalaj.http.Http

class MyCurrency(requests: Requests, APIKey : String) extends CacheApplicationCommandController(requests){

  //TODO store in DB in future
  var currency_symbol = "KZT"
  var currency_rate = 0.177672
  var currency_atm = 0.182
  var currency_cc = 0.184
  var currency_dollar = 0.002152

  def returnRate (currency : String, travelling_currency :String) = {
    val response = Http(s"https://api.apilayer.com/exchangerates_data/convert?to=${currency}&from=${travelling_currency}&amount=1")
      .param("apikey", APIKey)
      .asString
    println(response.body)
    val rates = ujson.read(response.body)
    rates("info")("rate").num
  }

  val groupCommand = SlashCommand.group("currency", "Currency Related Commands")(
    SlashCommand
      .withParams(string("name", "The name of the currency"))
      .command("set_name", "Sets the Currency Name"){  i =>
        currency_symbol = i.args
        sendMessage(s"Currency name is set to ${currency_symbol}")},
    SlashCommand
      .withParams(string("rate", "The rate against Rupee for Cash Withdrawal"))
      .command("set_atm", "Sets the currency Rate for ATM"){ implicit i =>
        currency_atm = i.args.toDouble
        sendMessage(s"New Rate for ${currency_symbol} = ${currency_atm} Rupees (CASH)")},
    SlashCommand
      .withParams(string("rate", "The rate against Rupee for Card Swipe"))
      .command("set_cc", "Sets the currency Rate for CC"){ implicit i =>
        currency_cc = i.args.toDouble
        sendMessage(s"New Rate for ${currency_symbol} = ${currency_cc} Rupees (CARD)")},
    SlashCommand
      .command("view", "Views the current Currency Settings"){ implicit i =>

        val currency_display = EmbedField("Rupee Rates", s"${currency_rate} Rupees\n${currency_atm} Rupees (ATM) \n${currency_cc} Rupees (Card) ")
        val cash_dollar = EmbedField("Dollar Conversion", s"${currency_dollar} Dollars")

        sendEmbed(
          embeds = Seq(
            OutgoingEmbed(
              title = Some(currency_symbol),
              fields = Seq(currency_display,cash_dollar),
              footer = Some(OutgoingEmbedFooter(""))
            )
          )
        )},
    SlashCommand
      .command("refresh", "Refreshes the Rates using API") { implicit i =>
        async( implicit token => {
          currency_dollar = returnRate("USD", currency_symbol)
          currency_rate = returnRate("INR", currency_symbol)
          //currency_rate = returnRate(currency_symbol)
          sendAsyncMessage(s"Refresh Completed")
        })
      }
  )

}