package org.brzy.webapp.controller

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait Constraint

case class Roles(allowed:String*) extends Constraint