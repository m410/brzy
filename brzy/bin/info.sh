#!/bin/sh
exec scala-2.8 -cp $BRZY_HOME/lib/brzy-all-0.1.jar:$BRZY_HOME/lib/jyaml-1.3.jar "$0" `pwd` $BRZY_HOME 
!#

import org.brzy.build._
import org.brzy.config.{Config,Builder}

object Info extends Application {
		println("Configuration:")
		val config = new Builder(args(0) + "/brzy-app.b.yml","development").config
    println(config.toString)
}
Info.main(args)
