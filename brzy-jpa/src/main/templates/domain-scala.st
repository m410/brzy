package $packageName$

import org.brzy.webapp.controller.CrudController
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
  @BeanProperty @Id
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "person_seq")
  @SequenceGenerator(name="person_seq", sequenceName = "person_seq", allocationSize = 1, initialValue= 1)
  var id:JLong = _

  @BeanProperty @Version var version:JInt = _
  @BeanProperty @NotNull var name:String = _
}

object $className$ extends JpaDao[$className$,JLong]

class $className$Controller extends CrudController("$attributeName$",$className$)