import ackcord._
import ackcord.commands.PrefixParser
import ackcord.data.GuildId
import ackcord.gateway.{Dispatch, GatewayIntents}
import ackcord.interactions.InteractionsRegistrar
import ackcord.requests.CreateMessage
import ackcord.syntax._

object OldBot extends App {

  val config = Config.config match {
    case Right(value) => value
    case Left(value) => sys.error(s"Failed to parse config - $value")
  }

  val token    = config.discordToken
  val myGuildId = config.guildId
  val settings = ClientSettings(token, intents = GatewayIntents.AllNonPrivileged)
  import settings.executionContext

  settings
    .createClient()
    .foreach { client =>
      client.onEventSideEffectsIgnore { case APIMessage.Ready(_, _, _) => println("Now ready") }
      client.onEventSideEffectsIgnore { case msg => println("Received Event : " + msg.getClass.getName)}

      {
        import client.system
        client.events.fromGatewaySubscribe.runForeach {
          case Dispatch(_, msg, _) => println("Event Dispatch " + msg.getClass.getName)
          case _                   =>
        }
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

      val myEvents      = new MyEvents(client.requests)
//      val myListeners   = new Listeners(client)
      val myCommands    = new MyCommands(client, client.requests)
      val myHelpCommand = new MyHelpCommand(client.requests)

      client.bulkRegisterListeners(
        myEvents.printReady,
        myEvents.welcomeNew
      )

//      client.registerListener(myListeners.createListeners)

      client.commands.runNewCommand(
        PrefixParser.structured(needsMention = true, Seq("!"), Seq("help")),
        myHelpCommand.command
      )

      client.commands.bulkRunNamedWithHelp(
        myHelpCommand,
        myCommands.hello,
        myCommands.copy,
        myCommands.setShouldMention,
        myCommands.modifyPrefixSymbols,
        myCommands.guildInfo,
        myCommands.sendFile,
        myCommands.adminsOnly,
        myCommands.timeDiff,
        myCommands.ping,
        myCommands.maybeFail,
        myCommands.ratelimitTest("ratelimitTest", client.requests.sinkIgnore[Any]),
        myCommands
          .ratelimitTest("ratelimitTestOrdered", client.requests.sinkIgnore[Any](Requests.RequestProperties.ordered)),
        myCommands.kill
      )

      val buttonCommands = new MyComponentCommands(client.requests)

      client.commands.bulkRunNamedWithHelp(
        myHelpCommand,
        buttonCommands.commands: _*
      )

      import client.system

      client.onEventSideEffectsIgnore { case APIMessage.Ready(applicationId, _, _) =>
        client.events.interactions
          .to(InteractionsRegistrar.gatewayInteractions()(applicationId.asString, client.requests))
          .run()
      }

      client.login()
    }
}