package org.brzy.email

import org.brzy.config.mod.ModProvider

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class EmailModPrivider(c:EmailModConfig) extends ModProvider {
  val name = c.name.get
  override val serviceMap = Map("emailService" -> new EmailService(c))
}