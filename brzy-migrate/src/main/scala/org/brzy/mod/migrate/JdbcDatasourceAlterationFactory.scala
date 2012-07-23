package org.brzy.mod.migrate

import javax.sql.DataSource

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */

trait JdbcDatasourceAlterationFactory extends AlterationFactory{
  def getAlterationWithDataSource(dataSource:DataSource):Alteration
}
