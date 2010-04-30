package org.brzy.build

import io.Source

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class BrzyAppBYml(name:String, version:String, group:String, artifact:String) {

  private val nameHolder = "[name]"
  private val versionHolder = "[version]"
  private val groupHolder = "[group]"
  private val artifactHolder = "[artifact]"

  val content = Source.fromURL(getClass.getClassLoader.getResource("template.brzy-app.b.yml"))
    .mkString
    .replaceAll(nameHolder,name)
    .replaceAll(versionHolder,version)
    .replaceAll(groupHolder,group)
    .replaceAll(artifactHolder,artifact)
}