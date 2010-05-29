

import org.brzy.config.{ConfigPrinter, Builder}

object Info extends Application {
	val config = new Builder(args(0) + "/brzy-webapp.b.yml","development").runtimeConfig
  ConfigPrinter(config)
}
Info.main(args)
