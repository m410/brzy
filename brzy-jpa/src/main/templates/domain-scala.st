package $packageName$

import org.brzy.mod.jpa.JpaPersistence
import javax.validation.constraints.{NotNull,Size}
import javax.persistence._
import reflect.BeanProperty

/**
 *
 */
@Entity
@Table(name="$className$")
class $className$ {
	@Id @BeanProperty var id:java.lang.Long = _
	@Version @BeanProperty var id:java.lang.Integer = _
	@NotNull @BeanProperty var firstName:String = _
	@NotNull @BeanProperty var lastName:String = _
}

object $className$ extends JpaPersistence