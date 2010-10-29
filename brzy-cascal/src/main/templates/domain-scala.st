package $packageName$

import com.shorrockin.cascal.model._

/**
 * @see http://github.com/shorrockin/cascal/wiki
 */
@Keyspace("Example")
@Family("StandardCF")
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