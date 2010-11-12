package $packageName$

import com.shorrockin.cascal.serialization.annotations.{Key, Keyspace, Family,Value}
import com.shorrockin.cascal.serialization.Converter
import com.shorrockin.cascal.session.{Insert,KeyRange}
import com.shorrockin.cascal.utils.UUID

import com.shorrockin.cascal.utils.Conversions._
import org.brzy.mod.cascal.Cascal
import org.brzy.webapp.action.args.Headers

import org.brzy.validator.Validator
import org.brzy.validator.constraints._

/**
 * @see http://github.com/shorrockin/cascal/wiki
 */
@Keyspace("Keyspace")
@Family("Family")
case class $className$(
    @Key val key:String,
    @Value("State") val state:String,
    @Value("Created") val created:Date)

object $className$ {
  def get(keyId:String):$className$ = {
    val session = Cascal.value.get
    val results = session.list("Keyspace1" \ "Standard2" \ keyId)
    Converter[Person](results)
  }

  
}