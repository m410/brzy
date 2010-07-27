package org.brzy.jpa

import util.DynamicVariable
import javax.persistence.EntityManager

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object JpaContext extends DynamicVariable(Option[EntityManager](null))