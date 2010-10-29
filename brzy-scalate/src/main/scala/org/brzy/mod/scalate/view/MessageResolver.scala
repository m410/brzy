package org.brzy.mod.scalate.view

import java.util.{ResourceBundle, Locale}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object MessageResolver {
  def message(ref:String,locale:Locale = Locale.getDefault) = {
    val bundle = ResourceBundle.getBundle("i18n/messages", locale)

    if(bundle.containsKey(ref))
      bundle.getString(ref)
    else
      ref
  }
}