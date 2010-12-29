package org.brzy.webapp.exp

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait Constraint

case class Roles(allowed:String*) extends Constraint