package org.brzy.build

import xml.transform.RuleTransformer
import org.brzy.config.AppConfig
import xml.{XML, Elem}
import collection.mutable.ListBuffer
import collection.JavaConversions._
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.ClasspathHelper._
import org.reflections.Reflections
import javax.persistence.Entity

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class PersistenceXml(config:AppConfig) {
  private val parentName = "persistence-unit"
  private val template = XML.load(getClass.getClassLoader.getResource("template.persistence.xml"))
  private val children = ListBuffer[Elem]()
	private val persistence = config.persistence.find(persist => persist.implementation == "scala-jpa").get
  private val reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(getUrlsForPackagePrefix(config.application.group_id))
      .setScanners(new TypeAnnotationsScanner()))

  private val entities:List[Class[_]] = reflections.getTypesAnnotatedWith(classOf[Entity]).toList

  entities.foreach(e => {
    children += <class>{e.getName}</class>
  })

  if(persistence.properties != null)
    children += <properties>
      {persistence.properties.map(f =>  <property name={f._1} value={f._2} />)}
    </properties>

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}