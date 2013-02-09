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
package org.brzy.webapp.action.args

import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import io.Source

import org.apache.commons.fileupload.servlet.ServletFileUpload
import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._
import java.io.File


/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait PostBody extends Arg {
  def asText: String

  def parameters: Map[String, String]

  def paramAsFile(name: String): FileItem
}

/**
 * Document me..
 */
class PostBodyRequest (request: HttpServletRequest,
        val maxSize:Long = 10000000,
        val tempDir:File = new File(util.Properties.tmpDir),
        val sizeThreshold:Int = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD) extends PostBody {

  def asText = Source.fromInputStream(request.getInputStream).mkString


  private lazy val fileItems = {
    if (ServletFileUpload.isMultipartContent(request)) {
      val factory = new DiskFileItemFactory()
      factory.setSizeThreshold(sizeThreshold)
      factory.setRepository(tempDir)
      val upload = new ServletFileUpload(factory)
      upload.parseRequest(request).toList
    }
    else {
      throw new NotMultipartPostException("Not a multipart upload")
    }
  }

  /**
   * Get non-file request parameters.
   */
  def parameters: Map[String, String] = fileItems.filter({
    case f: FileItem => {
      f.isFormField
    }
  }).map({
    case f: FileItem => {
      f.getFieldName -> f.getString
    }
  }).toMap


  /**
   * This uses the apache commons fileupload api to digest the multipart content of the body.  This
   * is used when uploading a file.  
   *
   * @param name The name of the parameter the data is uploaded as.
   */
  def paramAsFile(name: String): FileItem = {

    val item = fileItems.find({
      case i: FileItem => {
        !i.isFormField && i.getFieldName == name
      }
    })

    item match {
      case Some(i) => i.asInstanceOf[FileItem]
      case _ => throw new NoFileWithNameException("No File parameter with name:" + name)
    }
  }

  override def toString = {
    val a = fileItems.map({
      case f: FileItem => {
        if (f.isFormField)
          f.getFieldName -> f.getString
        else
          f.getFieldName -> "<File>"
      }
    }).toMap
    new StringBuilder()
            .append("PostBody")
            .append(a.mkString("[", ", ", "]"))
            .toString()
  }
}