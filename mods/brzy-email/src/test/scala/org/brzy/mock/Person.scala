package org.brzy.mock

import com.shorrockin.cascal.serialization.annotations.{Key, Keyspace, Family,Value}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Keyspace("Example")
@Family("StandardCF")
case class Person(
    @Key key:String,
		@Value("State") state:String)

