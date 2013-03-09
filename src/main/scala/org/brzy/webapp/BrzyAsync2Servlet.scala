package org.brzy.webapp

import org.brzy.webapp.application.WebApp

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet._

import org.slf4j.LoggerFactory
import java.util.UUID
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64


/**
 * This executes async actions in an async servlet.
 *
 * @author Michael Fortin
 */
class BrzyAsync2Servlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[BrzyServlet])

  override def init(config: ServletConfig) {
  }

  def acceptVal(key:String, v:String):String = {
    val cript = MessageDigest.getInstance("SHA-1")
    cript.reset()
    cript.update((key+v).getBytes("utf8"))
    new String(Base64.encodeBase64(cript.digest()))
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val webapp = request.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val action = webapp.serviceAction(request).getOrElse(throw new RuntimeException("error"))

    val responseTail = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

    val origin = request.getHeader("Origin")
    println(s"  ######origin:'$origin'")
    val key = request.getHeader("Sec-WebSocket-Key")
    println(s"  ######key:'$key'")

    val version = request.getHeader("Sec-WebSocket-Version")
    println(s"  ######version:'$version'")

    response.setContentType("text/plain")
    response.setCharacterEncoding("utf-8")
    response.setStatus(101)
    response.setHeader("Upgrade","websocket")
    response.setHeader("Connection","Upgrade")
    response.setHeader("Access-Control-Allow-Origin", "*")

    val socAccept = acceptVal(key, responseTail)
    println(s"  ######socAccept:'$socAccept'")

    response.setHeader("Sec-WebSocket-Accept", socAccept)


    val writer = response.getOutputStream
    // Id
    writer.print(UUID.randomUUID().toString)
    writer.print(';')

    // Padding
    for (i <- 0 to 1024) {
      writer.print(' ')
    }
    writer.print(';')
    writer.flush()



    println(s"  ###### async started: ${request.isAsyncStarted}")
    println(s"  ###### AsyncListener Trying async")

    val async = request.startAsync(request, response)
    async.addListener(new AsyncListener {
      def onError(p1: AsyncEvent) {     println(s"  ###### AsyncListener ERROR: $p1")}
      def onComplete(p1: AsyncEvent) {  println(s"  ###### AsyncListener COMPLETE: $p1")}
      def onStartAsync(p1: AsyncEvent) {println(s"  ###### AsyncListener START: $p1")}
      def onTimeout(p1: AsyncEvent) {   println(s"  ###### AsyncListener TIMEOUT: $p1")}
    })

    //    async.getResponse.getWriter.write("responded with zero")
    //    async.getResponse.getWriter.flush()

    async.start(new Runnable {
      def run() {

        println(s"  ###### AsyncListener Trying to respond")
        async.getResponse.setContentType("text/plain")
        response.setCharacterEncoding("utf-8")
        val s = "responded with inside run"
        response.setContentLength(s.getBytes.length)
        async.getResponse.getOutputStream.println(s)
        async.getResponse.flushBuffer()
        Thread.sleep(5000)
        println(s"  ###### AsyncListener Trying to respond again")
        async.getResponse.setContentType("text/plain")
        response.setCharacterEncoding("utf-8")
        val s1 = "responding again"
        response.setContentLength(s1.getBytes.length)
        async.getResponse.getOutputStream.println(s1)
        async.getResponse.flushBuffer()
        async.complete()
      }
    })
    //    action.trans.doWith(webapp.threadLocalSessions,()=>{
    //      log.debug("async doService:{}",action)
    //      action.doService(request, response)
    //    })
  }
}
