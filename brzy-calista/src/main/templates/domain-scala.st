package $packageName$

import org.brzy.calista.ocm._

/**
 * 
 */
case class $className$(key:String,p:String) extends StandardEntity[String]

object $className$ extends StandardDao[String,$className$] {
  def mapping = Mapping[$className$](
      "$className$",
      UTF8Serializer,
      Key(),
      Column("p"))
}