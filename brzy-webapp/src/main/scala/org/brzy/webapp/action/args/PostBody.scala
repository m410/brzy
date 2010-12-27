package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import collection.JavaConversions._
import org.apache.commons.fileupload.FileItem

import io.Source
import xml.XML
import com.twitter.json.Json


/**
 * This is not implemented yet.  This enables access to the the body of the post as a plain
 * text object to be used in parsing text, xml or json post data.
 *
 * @see http ://commons.apache.org/fileupload/using.html
 * @author Michael Fortin
 */
class PostBody(request: HttpServletRequest) {
  val maxSize = 100000
  val tempDir = new java.io.File(util.Properties.tmpDir)
  val sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD

  def asText = Source.fromInputStream(request.getInputStream).mkString

  def asXml = XML.load(request.getInputStream)

  def asJson = Json.parse(asText)

  def asBytes(name: String): Array[Byte] = {
    val factory = new DiskFileItemFactory()
    factory.setSizeThreshold(sizeThreshold)
    factory.setRepository(tempDir)
    val upload = new ServletFileUpload(factory)
    upload.setSizeMax(maxSize)
    val items = upload.parseRequest(request)

    val item = items.find(x => {
      val i = x.asInstanceOf[FileItem]
      i.isFormField && i.getFieldName == name
    })

    item match {
      case Some(i) => i.asInstanceOf[FileItem].get
      case _ => error("Not a byte array.")
    }
  }
}