package org.brzy.mod.migrate

import javax.sql.DataSource
import java.sql.{Connection, SQLException}

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */
trait DatabaseMigration {

  val versionTable = "application_versions"

  val versionQuery = "select max(created_on), version from " + versionTable

  def dataSource: DataSource

  def versions: List[Migrate]

  def upgradeToVersion(version: String) {

    // read last row
    val dbVersion = doIn {
      connection =>
        try {
          val result = connection.createStatement.executeQuery(versionQuery)

          if (result.next())
            toNumericVersion(result.getString(2))
          else
            new Version(0, 0, 0, 0)
        }
        catch {
          case s: SQLException => // missing table
            // create table
            // create default version
            new Version(0, 0, 0, 0)
        }
    }

    versions.sortBy(_.version).foreach(migrate => {
      val codeVersion = toNumericVersion(migrate.version)
      println(codeVersion)
      //
      //      if (codeVersion.compareTo(dbVersion) >= 0)
      //        migrate.up()
      //      else if (codeVersion.compareTo(dbVersion) < 0)
      //        migrate.down()
    })

    // set the database version
    true
  }

  def toNumericVersion(version: String) = {
    new Version(1, 2, 3, 4)
  }

  def downgradeToVersion(version: String) {
    true
  }

  def doIn(action: (Connection) => AnyRef): AnyRef = {
    val connection = dataSource.getConnection
    try {
      SQL.withValue(connection) {
        action(connection)
      }
    }
    finally {
      connection.close()
    }
  }

}

