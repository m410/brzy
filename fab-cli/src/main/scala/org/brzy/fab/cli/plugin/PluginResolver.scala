package org.brzy.fab.cli.plugin

import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.IvyContext
import org.apache.ivy.Ivy

import org.brzy.fab.file.File
import org.brzy.fab.print.Conversation
import org.brzy.fab.cli.dependency.Ivy._
import org.brzy.fab.cli.mod.BaseConf

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object PluginResolver {
  val cacheName = "plugins"
  val base = File(".brzy/" + cacheName)
  val settingsFile = File(".brzy/"+cacheName+"/ivysettings.xml")
  val ivyFile = File(".brzy/"+cacheName+"/ivy.xml")
  val retrievePattern = ".brzy/"+cacheName+"/[artifact]-[reversion]-([classifier]).[type]"

  def apply(config: BaseConf)(implicit line: Conversation): Unit = {

    if (!base.exists)
      base.mkdirs

    val settingsXml = new IvySettingsXml(config)
    settingsXml.saveToFile(settingsFile.getAbsolutePath)

    val ivyXml = new IvyXml(config)
    ivyXml.saveToFile(ivyFile.getAbsolutePath)

    doInIvyCallback((ivy: Ivy, context: IvyContext) => {
      ivy.configure(settingsFile)
      ivy.getResolveEngine.resolve(ivyFile)
      val modId = ModuleRevisionId.newInstance("org.brzy.fab", cacheName, "1.0")
      val options = new RetrieveOptions

      ivy.getRetrieveEngine.retrieve(modId, retrievePattern, options)
      null
    })
  }
}