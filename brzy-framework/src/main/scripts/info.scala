

import org.brzy.config.{ConfigPrinter, Builder}

object Info extends Application {
	val config = new Builder(args(0) + "/brzy-app.b.yml","development").runtimeConfig
  ConfigPrinter(config)
}
Info.main(args)
