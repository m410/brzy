/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.webapp.action


import java.lang.reflect.Method
import collection.mutable.Buffer
import java.util.regex.Pattern
import org.slf4j.LoggerFactory

import args._
import returns._
import org.brzy.webapp.view.FlashMessage

import javax.servlet.http.{HttpServletResponse => Response, HttpServletRequest => Request, Cookie}
import scala.collection.JavaConversions._
import collection.mutable.ListBuffer
import java.util.Enumeration
import java.lang.String


/**
 * This actions captures the information about a single controller action as part of the MVC
 * design pattern.  Action in brzy are a single instance that lives for the life of the
 * application.  They're created in the WebApp application class and made available to
 * the servlet.
 *
 * @author Michael Fortin
 */
class Action(val path: String, val actionMethod: Method, val inst: AnyRef, val viewType: String)
        extends Ordered[Action] {

  private[this] val log = LoggerFactory.getLogger(classOf[Action])
  private[this] val strPattern = "^/*" + path.replaceAll("""\{.*?\}""", """(.*?)""") + "$"

  val pattern = strPattern.r
  val returnTypes = actionMethod.getReturnType
  val parameterTypes = actionMethod.getParameterTypes

  /**
   * the default view to display if needed.
   */
  val defaultView = {
    val clazz = inst.getClass
    val folder: String =
    if (clazz.getSimpleName.indexOf("Controller") == -1)
      clazz.getSimpleName
    else
      clazz.getSimpleName.substring(0, clazz.getSimpleName.indexOf("Controller"))
    "/" + folder.substring(0, 1).toLowerCase + folder.substring(1) + "/" + actionMethod.getName
  }

  def matchParameters(url: String) = {
    log.debug("match parameters: {}, {}", strPattern, url)
    val buffer = Buffer[String]()
    val matcher = Pattern.compile(strPattern).matcher(url)
    if (matcher.find)
      for (i <- 1 to matcher.groupCount)
        buffer += matcher.group(i)
    buffer.toArray
  }

  val matchParameterIds = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile("""\{(.*?)\}""").matcher(path)
    while (matcher.find)
      buffer += matcher.group(1)
    buffer.toArray
  }

  override def compare(that: Action): Int = path.compareToIgnoreCase(that.path)

  override def toString = new StringBuilder().
      append("Action('").append(path).
      append("', ").append(actionMethod.getDeclaringClass.getName).
      append(".").append(actionMethod.getName).append(")").toString
}

/**
 * The object Companion object to the action class.  This contains many many helper functions
 * for actions used by the servlet.
 */
object Action {
  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val ParametersClass = classOf[Parameters]
  private[this] val SessionClass = classOf[Parameters]
  private[this] val HeadersClass = classOf[Headers]
  private[this] val WizardClass = classOf[Wizard]
  private[this] val CookiesClass = classOf[Cookies]

  /**
   * Action factory method.
   */
  def apply(path: String, actionMethod: Method, inst: AnyRef, viewType: String) = {
    new Action(path,actionMethod,inst,viewType)
  }
  
   /**
    * TODO need to hand the three return types, data, direction and stream
    */
   def handleResults(action:Action, result:AnyRef, req:Request, res:Response):Unit = {

     def matchData(result:Any):Unit = result match {
       case (s:String, m:AnyRef) =>
         log.debug("tuple: ({},{})",s,m)
         handleData(Model(s->m),req,res)
       case d:Data =>
         log.debug("Data: {}",d)
         handleData(d,req,res)
       case tup:(_,_) =>
         log.debug("tuple: {}",tup)
         tup.productIterator.foreach(s=>matchData(s))
       case r:(_,_,_) =>
         log.debug("tuple: {}",r)
         r.productIterator.foreach(s=>matchData(s))
       case r:(_,_,_,_) =>
         log.debug("tuple: {}",r)
         r.productIterator.foreach(s=>matchData(s))
       case r:(_,_,_,_,_) =>
         log.debug("tuple: {}",r)
         r.productIterator.foreach(s=>matchData(s))
       case _ => //ignore
     }

     matchData(result)

     // need to handle the direction after the data or a servlet error doesn't happen
     def matchDirection(result:Any):Unit = result match {
       case d:Direction =>
         log.debug("Direction: {}",d)
         handleDirection(action, d,req,res)
       case d:Data => // ignore it
       case (s:String, m:AnyRef) =>
         log.debug("Direction default: {}",action.defaultView)
         handleDirection(action, View(action.defaultView),req,res)
       case tup:(_,_) =>
         tup.productIterator.foreach(s=>matchDirection(s))
       case r:(_,_,_) =>
         r.productIterator.foreach(s=>matchDirection(s))
       case r:(_,_,_,_) =>
         r.productIterator.foreach(s=>matchDirection(s))
       case r:(_,_,_,_,_) =>
         r.productIterator.foreach(s=>matchDirection(s))
       case _ => // ignore
     }

     matchDirection(result)
   }

   /**
    * there can only be one direction
    */
   protected[action] def handleDirection(action:Action, direct:Direction, req:Request, res:Response) =
     direct match {
       case view:View =>
         val target: String = view.path + action.viewType
         log.debug("view: {}",target)
         req.getRequestDispatcher(target).forward(req,res)
       case f:Forward =>
         log.debug("forward: {}",f)
         req.getRequestDispatcher(f.path).forward(req,res)
       case s:Redirect =>
         val target: String = req.getContextPath + s.path
         log.debug("redirect: {}",s)
         res.sendRedirect(target)
       case s:Error =>
         log.debug("Error: {}",s)
         res.sendError(s.code,s.msg)
       case x:Xml =>
         log.debug("xml: {}",x)
         res.setContentType(x.contentType)
         res.getWriter.write(x.parse)
       case t:Text =>
         log.debug("text: {}",t)
         res.setContentType(t.contentType)
         res.getWriter.write(t.parse)
       case b:Binary =>
         log.debug("bytes: {}",b)
         res.setContentType(b.contentType)
         res.getOutputStream.write(b.bytes)
       case j:Json =>
         log.debug("json: {}",j)
         res.setContentType(j.contentType)
         res.getWriter.write(j.parse)
       case _ => error("Unknown Direction Type")
     }

   /**
    * There can be several data types
    */
   protected[action] def handleData(data:Data, req:Request, res:Response) =
     data match {
       case model:Model =>
         log.debug("model: {}",model)
         model.attrs.foreach(s=> req.setAttribute(s._1,s._2))
       case s:SessionAdd =>
         log.debug("sessionAdd: {}",s)
         s.attrs.foreach(nvp=>req.getSession.setAttribute(nvp._1,nvp._2))
       case s:Flash =>
         log.debug("flash: {}",s)
         new FlashMessage(s.code, req.getSession)
       case s:SessionRemove =>
         log.debug("sessionRemove: {}", s)
         req.getSession.removeAttribute(s.attr)
       case s:CookieAdd =>
         log.debug("cookieAdd: {}",s)
         res.addCookie(new Cookie(s.attrs._1,s.attrs._2.asInstanceOf[String]))
       case _ => error("Unknown Data Type")
     }

  /**
   *  
   */
   def buildArgs(action:Action, req:Request) = {
     val path = findActionPath(req.getRequestURI,req.getContextPath)
     val args = action.parameterTypes
     val list = new ListBuffer[AnyRef]()

     args.toList.foreach( arg => arg match {
         case ParametersClass =>
           val urlParams = action.matchParameters(path)
           val paramMap = new collection.mutable.HashMap[String,Array[String]]()

           for(i <- 0 to urlParams.size -1) {
             log.debug("add embeded param: ({},{})",action.matchParameterIds(i),urlParams(i))
             paramMap.put(action.matchParameterIds(i),Array(urlParams(i)))
           }
           val jParams = req.getParameterMap.asInstanceOf[java.util.Map[String, Array[String]]]
           val wrappedMap = new JMapWrapper[String,Array[String]](jParams)
           list += new Parameters( wrappedMap ++ paramMap)
         case SessionClass =>
           val session = new Session()
           val e = req.getSession.getAttributeNames.asInstanceOf[Enumeration[String]]

           while(e.hasMoreElements)
             session += e.nextElement->req.getAttribute(e.nextElement)
           list += session
         case HeadersClass =>
           val headers = new Headers()
           val e = req.getHeaderNames.asInstanceOf[Enumeration[String]]

           while(e.hasMoreElements)
             headers += e.nextElement->req.getHeader(e.nextElement)
           list += headers
         case WizardClass =>
           log.warn("wizard is not implemented")
         case CookiesClass =>
           log.warn("cookies is not implemented")
         case _ =>
           error("unknown action argument type: " + arg)
       }
     )
     log.debug("args: {}", list)
     list.toArray
   }

   def findActionPath(uri:String,ctx:String) =
     if(uri.endsWith(".brzy") && ctx =="")
       uri.substring(0,uri.length - 5)
     else if(uri.endsWith(".brzy") && ctx !="")
       uri.substring(ctx.length,uri.length - 5)
     else if(!uri.endsWith(".brzy") && ctx !="")
       uri.substring(ctx.length,uri.length)
     else
       uri


   def pathCompare(url:String)(action:Action):Boolean = {
     val actionPrePath = if (action.path.startsWith("/")) action.path else "/" + action.path
     val actionTokens:Array[String] = actionPrePath.replaceAll("//", "/").split("/")
     val urlTokens:Array[String] = url.replaceAll("//", "/").split("/")

     if(actionTokens.size == urlTokens.size){
       var map = Map[String,String]()

       for (x <- 0 to urlTokens.size -1)
         map += urlTokens(x) -> actionTokens(x)

       map.forall((nvp)=> if (nvp._1 == nvp._2 || nvp._2.startsWith("{")) true else false)
     }
     else false
   }

   def executeAction(action:Action, args:Array[AnyRef]) =
     if(args.size > 0)
       action.actionMethod.invoke(action.inst,args:_*)
     else
       action.actionMethod.invoke(action.inst)


}
