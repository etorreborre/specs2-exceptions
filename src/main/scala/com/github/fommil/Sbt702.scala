package com.github.fommil

import akka.contrib.jul.JavaLogging

object Sbt702 extends App with JavaLogging {

  log.info("if this appears across two lines, then the logging.properties is not being used")

  /* When running "sbt run" this produces

[error] Apr 04, 2013 10:58:58 AM java.lang.Thread getStackTrace
[error] INFO: if this appears across two lines, then the logging.properties is not being used

    Which is incorrect, even though we're passing the logging properties. If run manually (verbosity alert) i.e.

    sbt package
    java -Djava.util.logging.config.file=logging.properties -cp target/scala-2.10/classes:$HOME/.ivy2/cache/com.github.fommil/java-logging/jars/java-logging-1.0.jar:$HOME/.ivy2/cache/com.typesafe.akka/akka-contrib_2.10/jars/akka-contrib_2.10-2.1.2.jar:$HOME/.ivy2/cache/com.typesafe.akka/akka-actor_2.10/jars/akka-actor_2.10-2.1.2.jar:$HOME/.sbt/boot/scala-2.10.1/lib/scala-library.jar com.github.fommil.Sbt702

    Then the output is

    INFO: if this appears across two lines, then the logging.properties is not being used [java.lang.Thread] (com.github.fommil.Sbt702$)
   */
}
