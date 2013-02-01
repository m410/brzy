package org.brzy.exp

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait EmailProvider extends Application {
  val emailService = new EmailService
  abstract override def services = super.services ++ List(emailService)
}
