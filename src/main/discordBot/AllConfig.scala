import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

case class AllConfig(
                   discordToken: String,
                   guildId: String,
                   clientId: String,
                   currencyAPI: String
                 )

object AllConfig {
  val config: Result[AllConfig] = ConfigSource.default.load[AllConfig]
}
