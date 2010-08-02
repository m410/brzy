package org.brzy.cascal.mock

import com.shorrockin.cascal.serialization.annotations.{Key, Keyspace, Family,Value}
import com.shorrockin.cascal.serialization.Converter
import org.brzy.cascal.Cascal
import com.shorrockin.cascal.utils.Conversions._


@Keyspace("Keyspace1") // Driiiv
@Family("Standard1") // Hit|Creative|Suppression
case class Person(
    @Key key:String,
		@Value("firstName") firstName:String)

object Person {
  def get(keyId:String):Person = {
    println("key: " + keyId)
    val session = Cascal.value.get
    val results = session.list("Keyspace1" \ "Standard2" \ keyId)
    Converter[Person](results)
  }
}

