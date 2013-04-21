import sbt._
import sbt.Keys._
import scala._
import scala.Some
import play.Project._

object ShortenerBuild extends Build {

  val appName = "Shortener"
  val appVersion = "1.0"

  val appDependencies = Seq(
      "org.scalaz"          %% "scalaz-core"            % "6.0.4",
      "commons-validator"    % "commons-validator"      % "1.4.0"
  )

  val main = play.Project(
    appName, appVersion, appDependencies
  ).settings(
    scalacOptions += "-deprecation"
    // scalacOptions += "-Xlog-implicit-conversions"
  )
}