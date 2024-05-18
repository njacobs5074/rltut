ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

ThisBuild / resolvers += "jitpack" at "https://jitpack.io"

ThisBuild / libraryDependencies := Seq(
  "com.github.trystan" % "AsciiPanel" % "-SNAPSHOT"
)

lazy val root = (project in file("."))
  .settings(
    name := "rltut"
  )
