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

