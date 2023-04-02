import ackcord._
import ackcord.data.GuildId
import ackcord.interactions.InteractionsRegistrar
import ackcord.requests.CreateMessage
import ackcord.syntax._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MyScalaBot extends App {
  val config = AllConfig.config match {
    case Right(value) => value
    case Left(value) => sys.error(s"Failed to parse config - $value")
  }

  val token    = config.discordToken
  val guildId  = config.guildId
  val clientId = config.clientId
  val currencyAPI = config.currencyAPI

  val clientSettings = ClientSettings(token)
  //import clientSettings.executionContext

  val client = Await.result(clientSettings.createClient(), Duration.Inf)
  import client.executionContext

  client.onEventSideEffectsIgnore {
    case _: APIMessage.Ready => println("Now ready")
  }

  import client.requestsHelper._
  client.onEventAsync { implicit c =>
  {
    case APIMessage.ChannelCreate(_, channel, _, _) =>
      for {
        tChannel <- optionPure(channel.asTextChannel)
        _        <- run(tChannel.sendMessage("First"))
      } yield ()
    case APIMessage.ChannelDelete(optGuild, channel, _, _) =>
      for {
        guild <- optionPure(optGuild)
        _     <- runOption(guild.textChannels.headOption.map(_.sendMessage(s"${channel.name} was deleted")))
      } yield ()
    case APIMessage.ThreadCreate(_, thread, _, _) =>
      run(thread.sendMessage("First")).map(_ => ())
    case APIMessage.ThreadUpdate(_, thread, _, _) => run(thread.sendMessage(s"Edited")).map(_ => ())
    case msg @ APIMessage.ThreadDelete(_, threadId, parentId, _, _, _) =>
      run(
        CreateMessage
          .mkContent(parentId, s"Deleted thread ${msg.thread.fold(threadId.asString)(_.name)}")
      ).map(_ => ())
  }
  }

  val myCurrencyCommands = new MyCurrency(client.requests, currencyAPI)

  val myListeners = new MyListeners(client, myCurrencyCommands)
  client.registerListener(myListeners.createListeners)

  client.onEventSideEffectsIgnore {
    case msg: APIMessage.Ready =>

      // Create the commands globally in all discords.
      InteractionsRegistrar.createGlobalCommands(
        msg.applicationId, // Client ID
        client.requests,
        replaceAll = true, // Boolean whether to replace all existing
        // CreatedGuildCommand*
        myCurrencyCommands.groupCommand
      )

//      val myGuildId = GuildId(guildId)
//      InteractionsRegistrar.createGuildCommands(
//        msg.applicationId, // Client ID
//        myGuildId, // Guild ID
//        client.requests,
//        replaceAll = true, // Boolean whether to replace all existing
//        myCurrencyCommands.groupCommand
//      )
  }

  client.onEventSideEffectsIgnore { case msg: APIMessage.Ready =>
    client.runGatewayCommands(msg.applicationId.asString)(
      myCurrencyCommands.groupCommand
    )
  }

  client.login()
}
