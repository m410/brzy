package org.brzy.jpa

import util.DynamicVariable
import javax.persistence.EntityManager

/**
 * Thread local context that holds the the entity manager.
 * 
 * @author Michael Fortin
 */
object JpaContext extends DynamicVariable(Option[EntityManager](null))