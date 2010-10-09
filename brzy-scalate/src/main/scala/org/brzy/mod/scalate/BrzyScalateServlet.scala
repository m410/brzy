package org.brzy.mod.scalate

import org.fusesource.scalate.servlet.TemplateEngineServlet
import javax.servlet.ServletConfig

/**
 * Adds a few view functions to every ssp page via adding them to the  scalate
 * template engine.
 * 
 * @author Michael Fortin
 */
class BrzyScalateServlet extends TemplateEngineServlet {
  override def init(config: ServletConfig) = {
    super.init(config)
    templateEngine.importStatements ++= List(
      "implicit val implicitRequest = request",
      "import org.brzy.mod.scalate.ViewFunctions._"
      )
  }
}