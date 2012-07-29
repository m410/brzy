package org.brzy.mod.migrate

import util.DynamicVariable
import java.sql.Connection

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
object SQL extends DynamicVariable[Connection](null) {

  def execute(statement:String) = value.createStatement().execute(statement)

  //  def execute(query:String,args:String*) = value.createStatement().execute(query, args)

  def query(query:String) = value.createStatement().executeQuery(query)

  def update(query:String) = value.createStatement().executeUpdate(query)

  //  def update(query:String,args:String*) = value.createStatement().executeUpdate(query, args)

  def commit() {value.commit()}

  def rollback() {value.rollback()}
}
