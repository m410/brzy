

import org.brzy.build._
import org.brzy.config.Builder

object Info extends Application {
	println("Configuration (MOCKUP):")
	val config = new Builder(args(0) + "/brzy-app.b.yml","development").runtimeConfig
  println(config.toString)
}
Info.main(args)
