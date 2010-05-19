package org.brzy.scalate

import org.brzy.plugin.WebAppPlugin
import collection.JavaConversions._

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalatePluginResource(map:Map[_,_]) extends WebAppPlugin {

  def this(jmap:java.util.Map[_,_]) = this(jmap.toMap)

  
}