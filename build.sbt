// mkdir -p src/{main,test}/{java,scala,resources}/com/github/fommil/

name := "specs2-exceptions"

version := "1.0-SNAPSHOT"

organization := "com.github.fommil"

scalaVersion := "2.10.1"

resolvers ++= Seq(
                Resolver.mavenLocal,
                Resolver.sonatypeRepo("releases"),
                Resolver.sonatypeRepo("snapshots"),
                Resolver.typesafeRepo("releases"),
                Resolver.typesafeRepo("snapshots")
              )


libraryDependencies <<= scalaVersion { scala_version => 
    Seq(
        "com.github.fommil"    %  "java-logging"             % "1.0",
        "com.typesafe.akka"    %% "akka-contrib"             % "2.1.2" intransitive(),
        "com.typesafe.akka"    %% "akka-actor"               % "2.1.2",
        "org.specs2"           %% "specs2"                   % "1.13" % "test",
        "com.typesafe.akka"    %% "akka-testkit"             % "2.1.2" % "test"
    )
}

fork := true

javaOptions += "-Xmx2G"

javaOptions += "-Djava.util.logging.config.file=logging.properties"
