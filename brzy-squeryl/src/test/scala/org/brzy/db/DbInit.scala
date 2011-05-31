/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.db

import java.sql.{Connection, DriverManager}

/**
 * Initialize a Database for testing
 * 
 * @author Michael Fortin
 */
object DbInit {

  private var isDone = false;

  def init() {
    if(!isDone) {
      println("init: start")
      Class.forName("org.h2.Driver")
      val conn:Connection  = DriverManager.getConnection("jdbc:h2:target/test", "sa", "sa");
      conn.createStatement.execute("drop table if exists person")
      conn.commit()
      conn.createStatement.execute("""create table if not exists person (id int auto_increment primary key not null,first_name varchar(24) not null,last_name varchar(24) not null)""")
      conn.commit()
      conn.createStatement.execute("""insert into person (first_name, last_name) values ('test','user')""")
      conn.close()
      isDone = true
      println("init: done")
    }
  }
}