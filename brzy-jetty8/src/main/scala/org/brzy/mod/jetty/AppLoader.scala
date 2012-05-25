package org.brzy.mod.jetty

import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Settings, Global}
import java.io.{File, StringWriter, PrintWriter}

import org.slf4j.LoggerFactory
import actors.Futures._
import java.net.URLClassLoader


/**
 * Application Loader
 *
 * @author Michael Fortin
 */
class AppLoader private(sourceDir:File, classesDir:File, compilerPath:String, runPath:String, loaderClass:String) {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] var applicationLoader: URLClassLoader = _
  private[this] var lastModified = System.currentTimeMillis() - 1000 // need to round to the previous second
  private[this] var webApp:AnyRef = _

  log.debug("compilerPath:{}",compilerPath)
  log.debug("runPath:{}",runPath)

  private[this] val settings = {

    def error(s: String) {
      println("######## errors")
    }

    val settings = new Settings(error)
    settings.classpath.value = compilerPath
    settings.sourcedir.value = sourceDir.getAbsolutePath
    settings.outdir.value = classesDir.getAbsolutePath
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings
    settings
  }

  private[this] val errorBufferWriter = new StringWriter()
  private[this] val writer = new PrintWriter(errorBufferWriter)
  private[this] val reporter = new ConsoleReporter(settings,Console.in, writer)
  private[this] val compiler = new Global(settings, reporter)

  def childClassLoader = applicationLoader

  def reload(files:List[File]) = future {
    lastModified = System.currentTimeMillis() - 1000
    log.warn("changed files:{}",files)
    log.warn("Recompiling Source...")
    stopApplication()
    val (hasErrors, errorText) = recompileSource(files)
    webApp = makeApplication()

    if(hasErrors)
      CompilerError(errorText)
    else
      Running(webApp)
  }

  def sourceModified: Option[List[File]] = {
    val files = findFiles(sourceDir).filter(f=>{ f.lastModified() > lastModified})
    if (files.isEmpty) None else Option(files)
  }

  def makeApplication() = {
    val cp = runPath.split(":").filter(_ != "").map(f => { new File(f).toURI.toURL})
    applicationLoader = new URLClassLoader(cp, getClass.getClassLoader)
    java.lang.Thread.currentThread().setContextClassLoader(applicationLoader)
    log.debug("parent classloader: {}",getClass.getClassLoader)
    log.debug("sub application classpath: {}",cp)
    log.debug("sub application classloader: {}",applicationLoader)

    val clazz = applicationLoader.loadClass(loaderClass)
    val declaredConstructor = clazz.getDeclaredConstructor(Array.empty[Class[_]]: _*)
    declaredConstructor.setAccessible(true)
    val inst = declaredConstructor.newInstance()
    val method = clazz.getMethod("load", Array.empty[Class[_]]: _*)
    val a = method.invoke(inst)
    a.getClass.getMethod("startup").invoke(a)
    webApp = a
    a
  }

  def recompileSource(files: List[File]) =  {
    (new compiler.Run).compile(files.map(_.getAbsolutePath))
    log.debug("errors: {}, message: {}",reporter.hasErrors, errorBufferWriter.toString)

    val r = (reporter.hasErrors, errorBufferWriter.toString)
    reporter.flush()
    errorBufferWriter.getBuffer.setLength(0)
    r
  }

  def stopApplication() {
    webApp.getClass.getMethod("startup").invoke(webApp)
    webApp = null
  }


  private[this] def findFiles(root: File): List[File] = {
    if (root.isFile && root.getName.endsWith(".scala"))
      List(root)
    else
      makeList(root.listFiles).flatMap {f => findFiles(f)}
  }

  private[this] def makeList(a: Array[File]): List[File] = {
    if (a == null)
      Nil
    else
      a.toList
  }

}


object AppLoader {
  private[this] var inst:Option[AppLoader] = None
  
  def apply(sourceDir:File, classesDir:File, compilerPath:String, runPath:String, loaderClass:String) = {
    if (inst.isEmpty)
      inst = Option(new AppLoader(sourceDir, classesDir, compilerPath, runPath, loaderClass))
    inst.get
  }
}