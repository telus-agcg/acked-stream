import scala.util.Try
import scala.sys.process._

name := "acked-streams"

organization := "com.timcharper"

scalaVersion := "2.13.2"

scalacOptions += "-deprecation"

crossScalaVersions := Seq("2.12.11", "2.13.2")

val appProperties = {
  val prop = new java.util.Properties()
  IO.load(prop, new File("project/version.properties"))
  prop
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream"  % "2.6.15",
  "org.scalatest"     %% "scalatest"    % "3.1.1" % "test")

version := appProperties.getProperty("version")

homepage := Some(url("https://github.com/timcharper/acked-stream"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

publishArtifact in Test := false
publishArtifact in packageDoc := false

lazy val repo = "acked-stream"

publishMavenStyle := true
pomIncludeRepository := (_ => false)
resolvers += "GitHub Package Registry".at(s"https://maven.pkg.github.com/telus-agcg/$repo")
publishTo := Some(
  "GitHub Package Registry".at(s"https://maven.pkg.github.com/telus-agcg/$repo")
)
credentials ++= sys.env
  .get("GITHUB_TOKEN") // CI
  .orElse(Try("gh auth token".!!).toOption.map(_.trim)) // local
  .map(Credentials("GitHub Package Registry", "maven.pkg.github.com", "telus-agcg", _))