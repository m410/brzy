package org.brzy.db

import java.sql.{Connection, DriverManager}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */

object DbInit {

  private var isDone = false;

  def init = {
    if(!isDone) {
      println("init: start")
      Class.forName("org.h2.Driver")
      val conn:Connection  = DriverManager.getConnection("jdbc:h2:target/test", "sa", "sa");
      conn.createStatement.execute("drop table if exists person")
      conn.commit
      conn.createStatement.execute("""create table if not exists person (id int auto_increment primary key not null,first_name varchar(24) not null,last_name varchar(24) not null)""")
      conn.commit
      conn.createStatement.execute("""insert into person (first_name, last_name) values ('test','user')""")
      conn.close()
      isDone = true
      println("init: done")
    }
  }
}