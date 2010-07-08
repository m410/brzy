package org.brzy.tomcat

import scala.util.parsing.input.{NoPosition, Position}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
case class CompilerError(
        file: String,
        message: String,
        pos: Position = NoPosition,
        original: CompilerError = null)
