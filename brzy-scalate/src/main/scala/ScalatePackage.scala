import org.fusesource.scalate.TemplateSource
import org.fusesource.scalate.Binding
import org.fusesource.scalate.support.TemplatePackage

/**
 * Defines some common imports, attributes and methods across all templates.
 */
class ScalatePackage extends TemplatePackage {
   def header(source: TemplateSource, bindings: List[Binding]) = """
import org.brzy.mod.scalate.view.UrlFunctions._
import org.brzy.mod.scalate.view.FlashFunctions._
import org.brzy.mod.scalate.view.FormFunctions._
import org.brzy.mod.scalate.view.SecureFunctions._
import org.brzy.mod.scalate.view.InternationalizationFunctions._
import scala.collection.JavaConversions._
implicit val req = request
"""
}