import ackcord.Requests
import ackcord.interactions._
import ackcord.interactions.commands._
import ackcord.util.JsonSome

class AllSlashCommands(requests: Requests) extends CacheApplicationCommandController(requests) {

  val pongCommand = SlashCommand.command("ping", "Check if the bot is alive") { _ =>
    sendMessage("Pong")
  }

  val echoCommand = SlashCommand
    .withParams(string("message", "The message to send back"))
    .command("echo", "Echoes a message you send") { implicit i =>
      sendMessage(s"${i.args}")
    }

  // Multiple arguments
  val multiArgsCommand = SlashCommand
    .withParams(string("message", "The message to send back") ~ string("intro", "The start of the message"))
    .command("echoWithPrefix", "Echoes a message you send") { implicit i =>
      sendMessage(s"${i.args._1}: ${i.args._2}")
    }

  // Optional arguments
  val optArgsCommand = SlashCommand
    .withParams(string("message", "The message to send back").notRequired)
    .command("echoOptional", "Echoes an optional message you send") { implicit i =>
      sendMessage(s"ECHO: ${i.args.getOrElse("No message")}")
    }

  val autocompleteCommand = SlashCommand
    .withParams(string("auto", "An autocomplete parameter").withAutocomplete(s => Seq(s * 2, s * 3, s * 4)))
    .command("simple-autocomplete", "A simple autocomplete command") { i =>
      sendMessage(s"Res: ${i.args}")
    }

  val asyncCommand = SlashCommand.command("async", "An async test command") { implicit i =>
    async(implicit token => sendAsyncMessage("Async message"))
  }

  val asyncEditCommand = SlashCommand
    .withParams(string("par1", "The first parameter") ~ string("par2", "The second parameter"))
    .command("asyncEdit", "An async edit test command") { implicit i =>
      sendMessage("An instant message").doAsync { implicit token =>
        editOriginalMessage(content = JsonSome("An instant message (with an edit)"))
      }
    }

  val groupCommand = SlashCommand.group("group", "Group test")(
    SlashCommand.command("foo", "Sends foo")(_ => sendMessage("Foo")),
    SlashCommand.command("bar", "Sends bar")(_ => sendMessage("Bar"))
  )


}