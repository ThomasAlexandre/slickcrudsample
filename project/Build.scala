import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "slickcrudsample"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "com.typesafe" % "slick_2.10.0-RC1" % "0.11.2",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.h2database" % "h2" % "1.3.166",
    "org.xerial" % "sqlite-jdbc" % "3.6.20",
    "org.apache.derby" % "derby" % "10.6.1.0",
    "org.hsqldb" % "hsqldb" % "2.0.0",
    "postgresql" % "postgresql" % "8.4-701.jdbc4",
    "mysql" % "mysql-connector-java" % "5.1.13")

  val main = play.Project(appName, appVersion, appDependencies).settings( // Add your own project settings here      
  )

}
