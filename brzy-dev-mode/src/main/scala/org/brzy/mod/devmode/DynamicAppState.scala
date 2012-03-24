package org.brzy.mod.devmode

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
sealed trait DynamicAppState

object Running extends DynamicAppState

object Compiling extends DynamicAppState

case class CompilerError(message:String) extends DynamicAppState
