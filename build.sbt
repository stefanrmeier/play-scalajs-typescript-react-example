name := """example"""
organization := "com.example"

retrieveManaged := false
scalacOptions in ThisBuild ++= Seq("-feature")

version := "1.0"

lazy val scalaV = "2.11.8"

scalaVersion := scalaV

import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences._

lazy val shared = crossProject.crossType(CrossType.Pure).
  settings(
    scalaVersion := scalaV,
    version := "1.0"
  ).
  jsConfigure(_ enablePlugins ScalaJSWeb )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val jvm = (project in file("jvm")).
  settings(
    scalaVersion := scalaV,
    version := "1.0",
    scalaJSProjects := Seq(js),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    resolvers ++= Seq(Resolver.jcenterRepo, Resolver.sonatypeRepo("snapshots")),
    libraryDependencies ++= Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
      "org.apache.commons" % "commons-lang3" % "3.0",
      "mysql"           % "mysql-connector-java"          % "5.1.40",
      "org.scalikejdbc" %% "scalikejdbc"                  % "2.5.0",
      "org.scalikejdbc" %% "scalikejdbc-config"           % "2.5.0",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",
      "org.scalikejdbc" %% "scalikejdbc-test"             % "2.5.0",
      "org.apache.poi" % "poi"                            % "3.15",
      "org.apache.poi" % "poi-ooxml"                      % "3.15",
      "org.apache.poi" % "poi-ooxml-schemas"              % "3.15",
      filters,
      "com.vmunier" %% "scalajs-scripts" % "1.0.0",
      "org.webjars" %% "webjars-play" % "2.5.0-4",
      "org.webjars.npm" % "react" % "15.4.1",
      "org.webjars" % "react" % "15.3.2",
      "org.webjars" % "bootstrap" % "3.1.1-2",
      "com.google.api-client"      % "google-api-client"               % "1.22.0"  % "compile",
      "com.google.oauth-client"    % "google-oauth-client-jetty"       % "1.22.0"  % "compile",
      "com.google.apis"             % "google-api-services-sheets"      % "v4-rev40-1.22.0"  % "compile",
      "com.google.apis" % "google-api-services-drive" % "v3-rev55-1.22.0" % "compile",
      "com.mohiva" %% "play-silhouette" % "4.0.0",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
      "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
      "net.codingwell" %% "scala-guice" % "4.0.1",
      "com.iheart" %% "ficus" % "1.2.6",
      "com.typesafe.play" %% "play-mailer" % "5.0.0",
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.5.0-akka-2.4.x",
      "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
      "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
      "be.objectify" %% "deadbolt-scala" % "2.5.1",
      "com.overzealous"    % "remark"             % "1.0.0",
      "com.github.rjeschke" % "txtmark"            % "0.13",
      ws,
      "com.brsanthu"       % "google-analytics-java" % "1.1.2",
      "com.mixpanel"       % "mixpanel-java"      % "1.4.4",
      "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
      "com.typesafe.play" %% "play-mailer" % "5.0.0",
      "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.3",
      "com.stripe" % "stripe-java" % "4.9.1",
      "com.github.seratch" %% "awscala" % "0.6.0",
      specs2 % Test,
      cache
    ),
    scalikejdbcSettings,
    // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
    EclipseKeys.preTasks := Seq(compile in Compile),

    maintainer in Linux := "Stefan Meier",
    packageSummary in Linux := "play-scalajs-typescript-react-example",
    packageDescription := "play-scalajs-typescript-react-example",
    rpmRelease := "1",
    rpmVendor := "example.com",
    rpmUrl := Some("https://github.com/stefanrmeier/play-scalajs-typescript-react-example"),
    rpmLicense := Some("Apache v2"),
    packageName := "example",

    javaOptions in Universal ++= Seq(
      // Since play uses separate pidfile we have to provide it with a proper path
      // name of the pid file must be play.pid
      s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

      // file with environment specific settings ( includes application.conf and then overrides some settings )
      s"-Dconfig.resource=environment.conf"
    ),
    routesGenerator := InjectedRoutesGenerator,
    routesImport += "utils.route.Binders._",
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ),
    defaultScalariformSettings,
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(FormatXml, false)
      .setPreference(DoubleIndentClassDeclaration, false)
      .setPreference(DanglingCloseParenthesis, Preserve),
    libraryDependencies += "com.github.ghik" %% "silencer-lib" % "0.5",
    addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % "0.5")
  ).
  enablePlugins(PlayScala, RpmPlugin).
  dependsOn(sharedJVM)

lazy val js = (project in file("js")).
  settings(
    scalaVersion := scalaV,
    version := "1.0",
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    )
  ).
  enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJS)

// loads the server project at sbt startup
onLoad in Global := (Command.process("project jvm", _: State)) compose (onLoad in Global).value

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default



