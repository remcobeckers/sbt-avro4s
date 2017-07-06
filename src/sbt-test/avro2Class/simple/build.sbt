import sbt._
import Keys._
import complete.DefaultParsers._

lazy val root = (project in file("."))
  .settings(
    name := "avro2Class",
    version := "0.1",
    scalaVersion := "2.10.5"
  ).enablePlugins(Avro4sSbtPlugin)

val checkUnchanged = inputKey[Unit]("checkUnchanged")
val fileLastModified = collection.mutable.Map.empty[String, Long]

checkUnchanged := {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  val target = args.head
  val lastModified = file(target).lastModified
  if (!fileLastModified.contains(target)) {
    fileLastModified += target -> lastModified
    // On Linux based systems lastModified is only accurate on the second.
    // To make sure that the next time the timestamp would have changed we need to wait a second
    Thread.sleep(1000)
  } else if (fileLastModified(target) != lastModified) {
    error(s"File $target was changed. Last modification times: ${fileLastModified(target)} vs. $lastModified")
  } else ()
}
