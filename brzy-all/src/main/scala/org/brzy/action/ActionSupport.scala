package org.brzy.action

import args._
import org.brzy.util.FlashMessage
import returns._
import javax.servlet.http.{HttpServletResponse => Response, HttpServletRequest => Request, Cookie}
import org.slf4j.LoggerFactory
import java.util.Enumeration
import scala.collection.JavaConversions._
import collection.mutable.ListBuffer


/**
 *
 * @author Michael Fortin
 * @version $Id: $
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
  def handleResults(action:Action, result:Any, req:Request, res:Response):Unit = {
    var hasDirection = false

    def doMatch(result:Any):Unit = result match {
      case (s:String, m:AnyRef) =>
        log.debug("tuple: ({},{})",s,m)
        handleData(Model(s->m),req,res)
      case d:Data =>
        log.debug("Data: {}",d)
        handleData(d,req,res)
      case d:Direction =>
        log.debug("Direction: {}",d)

        if(hasDirection)
          error("Only one Direction can be declared in the action returns")

        hasDirection = true
        handleDirection(d,req,res)
      case tup:(_,_) =>
        log.debug("tuple: {}",tup)
        tup.productIterator.foreach(s=>doMatch(s))
      case r:(_,_,_) =>
        log.debug("tuple: {}",r)
        r.productIterator.foreach(s=>doMatch(s))
      case r:(_,_,_,_) =>
        log.debug("tuple: {}",r)
        r.productIterator.foreach(s=>doMatch(s))
      case r:(_,_,_,_,_) =>
        log.debug("tuple: {}",r)
        r.productIterator.foreach(s=>doMatch(s))
      case _ => error("no match")
    }

    doMatch(result)

    if(!hasDirection)
      handleDirection(View(action.defaultView),req,res)
  }

  /**
   * there can only be one direction
   */
  protected def handleDirection(direct:Direction, req:Request, res:Response) =
    direct match {
      case view:View =>
        log.debug("view: {}",view)
        req.getRequestDispatcher(view.path).forward(req,res)
      case f:Forward =>
        log.debug("forward: {}",f)
        req.getRequestDispatcher(f.path).forward(req,res)
      case s:Redirect =>
        log.debug("redirect: {}",s)
        res.sendRedirect(s.path)
      case x:Xml =>
        log.debug("xml: {}",x)
      case t:Text =>
        log.debug("text: {}",t)
      case b:Bytes =>
        log.debug("bytes: {}",b)
      case j:Json =>
        log.debug("json: {}",j)
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
    val path = req.getRequestURI
    val args = action.parameterTypes
    val list = new ListBuffer[AnyRef]()
    
    args.toList.foreach( arg => arg match {
        case ParametersClass =>
          var p = new Parameters()
          req.getParameterMap.keysIterator.foreach(x =>{
            val y = req.getParameterMap.get(x)
            p.put(x.asInstanceOf[String],y.asInstanceOf[Array[String]])
          })
          val urlParams = action.matchParameters(path)
          for(i <- 0 to urlParams.size -1)
            p.put(action.matchParameterIds(i),Array(urlParams(i)))
          list += p
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
          log.debug("wizard")
        case CookiesClass =>
          log.debug("cookies")
        case _ => error("unknown action argument type: " + arg) //throw new UnknownActionArgumentException(x)
      }
    )
    list.toArray
  }

  def findActionPath(uri:String,ctx:String) = {
    if(uri.endsWith(".brzy") && ctx =="")
      uri.substring(0,uri.length - 5)
    else if(uri.endsWith(".brzy") && ctx !="")
      uri.substring(ctx.length,uri.length - 5)
    else if(!uri.endsWith(".brzy") && ctx !="")
      uri.substring(ctx.length,uri.length)
    else
      uri
  }



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