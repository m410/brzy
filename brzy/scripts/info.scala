

import org.brzy.build._
import org.brzy.config.{Config,Builder}

object Info extends Application {
	println("Configuration:")
	val config = new Builder(args(0) + "/brzy-app.b.yml","development").config
  println(config.toString)
}
Info.main(args)
