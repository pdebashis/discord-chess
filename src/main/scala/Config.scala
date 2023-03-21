import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

case class Config(
                   discordToken: String,
                   guildId: String,
                   clientId: String,
                   currencyAPI: String
                 )

object Config {
  val config: Result[Config] = ConfigSource.default.load[Config]
}
