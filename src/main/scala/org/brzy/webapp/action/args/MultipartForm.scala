package org.brzy.webapp.action.args

import org.apache.commons.fileupload.FileItem
import org.slf4j.LoggerFactory
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import io.Source
import xml.{XML, Elem}
import com.twitter.json.{Json => tJson}
import org.apache.commons.fileupload.servlet.ServletFileUpload
import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait MultipartForm  extends Arg {
  def asText:String 

  def asXml:Elem

  def asJson:AnyRef

  def paramAsFile(name: String): FileItem
}


class MultipartRequest protected  (request:HttpServletRequest) extends MultipartForm {
  private[this] val log = LoggerFactory.getLogger(classOf[MultipartForm])
  protected[this] val maxSize = 10000000
  protected[this] val tempDir = new java.io.File(util.Properties.tmpDir)
  protected[this] val sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD

  def asText = Source.fromInputStream(request.getInputStream).mkString

  def asXml = XML.load(request.getInputStream)

  def asJson = tJson.parse(asText).asInstanceOf[AnyRef]

  /**
   * Get non-file request parameters.
   */
  def parameters:Map[String, String] = {
    val factory = new DiskFileItemFactory()
    factory.setSizeThreshold(sizeThreshold)
    factory.setRepository(tempDir)
    val upload = new ServletFileUpload(factory)
    val items = upload.parseRequest(request)
    items.filter(_.asInstanceOf[FileItem].isFormField).map({case f:FileItem =>{
      f.getFieldName -> f.getString
    }}).toMap
  }

  /**
   * This uses the apache commons fileupload api to digest the multipart content of the body.  This
   * is used when uploading a file.  
   *
   * @param name The name of the parameter the data is uploaded as.
   */
  def paramAsFile(name: String): FileItem = {
    if (ServletFileUpload.isMultipartContent(request)) {
      val factory = new DiskFileItemFactory()
      factory.setSizeThreshold(sizeThreshold)
      factory.setRepository(tempDir)
      val upload = new ServletFileUpload(factory)
      upload.setSizeMax(maxSize)
      val items = upload.parseRequest(request)

      val item = items.find(x => {
        val i = x.asInstanceOf[FileItem]
        !i.isFormField && i.getFieldName == name
      })

      item match {
        case Some(i) => i.asInstanceOf[FileItem]
        case _ => throw new NoFileWithNameException("No File parameter with name:"+name)
      }
    }
    else {
      error("Not a multipart upload")
    }
  }
}