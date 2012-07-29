package org.brzy.mod.migrate

import javax.sql.DataSource

/**
 * Seems kind of convoluted.  Maybe come up with some alternate to the factory scheme.
 *
 * @author Michael Fortin
 */
class Migrations {

  val versions = Map(
    "1.0.0"-> new Version100()
  )

  class Version100 extends Alterations {
    def alterationFactories = List(
      new JdbcAlterationFactory()
    )
  }
}

class JdbcAlterationFactory extends JdbcDatasourceAlterationFactory {
  def getAlterationWithDataSource(dataSource: DataSource) = {
    new Alteration {
      def up() {}

      def down() {}
    }
  }
}
