import sbt._
import Keys._

import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
import sbtunidoc.Plugin.unidocSettings
import ohnosequences.sbt.SbtS3Resolver.S3Resolver
import ohnosequences.sbt.SbtS3Resolver.{ s3, s3resolver }

object JacksonCaseClassModuleBuild extends Build {

//////////////////////////////////////////////////////////////////////////////
// PROJECT INFO
//////////////////////////////////////////////////////////////////////////////

  val ORGANIZATION = "mesosphere"
  val PROJECT_NAME = "jackson-case-class-module"
  val PROJECT_VERSION = "0.1.0-SNAPSHOT"
  val SCALA_VERSION = "2.10.4"


//////////////////////////////////////////////////////////////////////////////
// DEPENDENCY VERSIONS
//////////////////////////////////////////////////////////////////////////////

  val JACKSON_MODULE_SCALA_VERSION = "2.3.2"
  val SCALATEST_VERSION = "2.1.0"


//////////////////////////////////////////////////////////////////////////////
// ROOT PROJECT
//////////////////////////////////////////////////////////////////////////////

  lazy val root = Project(
    id = PROJECT_NAME,
    base = file("."),
    settings = commonSettings ++ unidocSettings ++ publishSettings
  )

//////////////////////////////////////////////////////////////////////////////
// SETTINGS
//////////////////////////////////////////////////////////////////////////////

  lazy val commonSettings =
    Project.defaultSettings ++
    basicSettings ++
    scalariformSettings ++
    customFormatSettings

  lazy val publishSettings = S3Resolver.defaults ++ Seq(
    publishTo := Some(s3resolver.value("Mesosphere Public Repo", s3("downloads.mesosphere.io/maven")))
  )

  lazy val basicSettings = Seq(
    version := PROJECT_VERSION,
    organization := ORGANIZATION,
    scalaVersion := SCALA_VERSION,

    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % JACKSON_MODULE_SCALA_VERSION,
      "org.scala-lang" % "scala-reflect" % SCALA_VERSION,
      "org.scalatest" %% "scalatest" % SCALATEST_VERSION % "test"
    ),

    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),

    parallelExecution in Test := false,

    fork in Test := true
  )

  def customFormatSettings = Seq(
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(IndentWithTabs, false)
      .setPreference(IndentSpaces, 2)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(CompactControlReadability, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(FormatXml, true)
  )

}
