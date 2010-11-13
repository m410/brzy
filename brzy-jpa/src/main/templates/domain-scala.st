package $packageName$

import org.brzy.mod.jpa.JpaDao
import reflect.BeanProperty

import java.lang.{Long=>JLong,Integer=>JInt}
import java.util.{List=>JList,Map=>JMap}

import javax.persistence._
import javax.validation.constraints._

/**
 *
 */
@Entity
@Table(name="$className$")
class $className$ {
	@BeanProperty @Id var id:JLong = _
	@BeanProperty @Version var version:JInt = _
	@BeanProperty @NotNull var name:String = _
}

object $className$ extends JpaDao[$className$,JLong]