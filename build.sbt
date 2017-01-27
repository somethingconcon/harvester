
lazy val root = (project in file(".")).settings(
  
  name := "harvest-scheduler",
  version := "0.0.1-SNAPSHOT",
  credentials += Credentials(
    host     = sys.env("IVY2_HOST"),
    passwd   = sys.env("IVY2_PASSWORD"),
    realm    = "Repository Archiva Managed geezeo Repository",
    userName = sys.env("IVY2_USER")),
  organization := "geezeo",
  publishTo := Some(
    "geezeo" at sys.env("IVY2_BOB"),
  scalacOptions ++= Seq(
    "-deprecation", "-feature", "-Xmax-classfile-name", "128"),
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    // monix
    "io.monix" %% "monix" % "2.1.2",
    "io.monix" %% "monix-types" % "2.1.2",
    "io.monix" %% "monix-execution" % "2.1.2",
    "io.monix" %% "monix-eval" % "2.1.2",
    "io.monix" %% "monix-reactive" % "2.1.2",
    "io.monix" %% "monix-scalaz-72" % "2.1.2",
    // akka
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-actor" % "2.4.16",
    "com.typesafe.akka" %% "akka-agent" % "2.4.16",
    "com.typesafe.akka" %% "akka-camel" % "2.4.16",
    // "com.typesafe.akka" %% "akka-cluster" % "2.4.16",
    // "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.16",
    // "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.16",
    // "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.16",
    // "com.typesafe.akka" %% "akka-contrib" % "2.4.16",
    // "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.16",
    // "com.typesafe.akka" %% "akka-osgi" % "2.4.16",
    "com.typesafe.akka" %% "akka-persistence" % "2.4.16",
    "com.typesafe.akka" %% "akka-persistence-tck" % "2.4.16",
    "com.typesafe.akka" %% "akka-remote" % "2.4.16",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",
    "com.typesafe.akka" %% "akka-stream" % "2.4.16",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.16",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.16",
    "com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.16",
    "com.typesafe.akka" %% "akka-typed-experimental" % "2.4.16",
    "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.16",
    "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-jackson" % "10.0.1", // probably won't need this
    "com.typesafe.akka" %% "akka-http-xml" % "10.0.1",
    
    // leveldb for persistence (extension included in akka-persistence)
    // maybe use redis instead
    "org.iq80.leveldb"            % "leveldb"          % "0.7",
    "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",

    // common stuff
    "com.typesafe"  % "config"       % "1.3.0",
    "joda-time"     % "joda-time"    % "2.9.2",
    "org.joda"      % "joda-convert" % "1.8.1",
    "org.scalaz"   %% "scalaz-core"  % "7.2.8",
    
    // get me some shapeless
    // Test Resources
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
    "org.scalatest" %% "scalatest"                   % "2.2.6" % "test"))

addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.0")