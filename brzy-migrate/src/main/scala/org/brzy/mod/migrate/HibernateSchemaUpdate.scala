package org.brzy.mod.migrate

import org.hibernate.ejb.Ejb3Configuration
import org.hibernate.tool.hbm2ddl.SchemaUpdate

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait HibernateSchemaUpdate {
  val persistenceUnitName = "persistence-brzy"
  def autoUpdateSchema() {
    val ejbConfig = new Ejb3Configuration().configure(persistenceUnitName)
    new SchemaUpdate(ejbConfig.getHibernateConfiguration).execute(true, true)
  }
}
