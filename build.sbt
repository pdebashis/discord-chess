name := "discord-chess"
version := "0.1"
scalaVersion := "2.13.10"

resolvers += Resolver.JCenterRepository

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.github.pureconfig" %% "pureconfig" % "0.14.0",
  "net.katsstuff" %% "ackcord"                 % "0.18.1", //For high level API, includes all the other modules
  "net.katsstuff" %% "ackcord-core"            % "0.18.1", //Low level core API
  "net.katsstuff" %% "ackcord-commands"        % "0.18.1", //Low to mid level Commands API
  "net.katsstuff" %% "ackcord-lavaplayer-core" % "0.18.1",  //Low level lavaplayer API
  "org.scalaj" %% "scalaj-http" % "2.4.2",  // for HTTP API calls
  "com.lihaoyi" %% "ujson" % "1.4.2"  // for parsing HTTP API call responses
)

resolvers += "LavaPlayerRepo" at "https://m2.dv8tion.net/releases"
libraryDependencies += "com.sedmelluq" % "lavaplayer" % "1.3.77"

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.last
  case path if path.endsWith("/module-info.class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}