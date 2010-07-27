package org.brzy.mvc.action

import args._
import returns._
import org.brzy.mvc.view.FlashMessage

import javax.servlet.http.{HttpServletResponse => Response, HttpServletRequest => Request, Cookie}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import collection.mutable.ListBuffer
import java.util.Enumeration


/**
 *
 * @author Michael Fortin
 */
object ActionSupport {
  private val log = LoggerFactory.getLogger(getClass)

  private val ParametersClass = classOf[Parameters]
  private val SessionClass = classOf[Parameters]
  private val HeadersClass = classOf[Headers]
  private val WizardClass = classOf[Wizard]
  private val CookiesClass = classOf[Cookies]

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
  protected def handleDirection(action:Action, direct:Direction, req:Request, res:Response) =
    direct match {
      case view:View =>
        log.debug("view: {}",view)
        req.getRequestDispatcher(view.path + action.viewType).forward(req,res)
      case f:Forward =>
        log.debug("forward: {}",f)
        req.getRequestDispatcher(f.path).forward(req,res)
      case s:Redirect =>
        log.debug("redirect: {}",s)
        res.sendRedirect(req.getContextPath + s.path)
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
  protected def handleData(data:Data, req:Request, res:Response) =
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
    val pathTokens:Array[String] = action.path.split("""/""")
    val tokens:Array[String] = url.split("""/""")

    if(pathTokens.size == tokens.size){
      var map = Map[String,String]()

      for (x <- 0 to tokens.size -1)
        map += tokens(x) -> pathTokens(x)

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