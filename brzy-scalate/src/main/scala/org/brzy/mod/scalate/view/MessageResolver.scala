package org.brzy.mod.scalate.view

import java.util.{ResourceBundle, Locale}

/**
 * Resolves internationalized messages.
 * 
 * @author Michael Fortin
 */
object MessageResolver {
  val bundle = ResourceBundle.getBundle("i18n/messages", locale)

  def message(ref:String,locale:Locale = Locale.getDefault) = if(bundle.containsKey(ref))
      bundle.getString(ref)
    else
      ref

}