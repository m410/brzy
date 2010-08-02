// ---- jpa ----
  def persistenceXmlAction(args: List[String]) = runTask(Some("org.brzy.jpa.build.PersistenceXmlMain"), persistenceClasspath, args)

  lazy val persistenceXml = {
    val outDir = {webappPath/"WEB-INF"/"classes"/"META-INF"/}.toString
    val config = path("brzy-webapp.b.yml").toString
    persistenceXmlAction(List(config,"development",outDir))
  }.dependsOn(prePackage) describedAs "Generates the persistence.xml file"
// ---- jpa ----