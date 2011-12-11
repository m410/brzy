package org.brzy.webapp.action

import args.PostBodyRequest
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import java.io.FileInputStream
import xml.Elem
import org.springframework.mock.web.{MockHttpServletRequest, MockMultipartHttpServletRequest, MockMultipartFile, MockHttpServletResponse}
import io.Source
import org.junit.{Ignore, Test}


class FileUploadTest extends JUnitSuite {
  val boundary = "AaB03x"
  val content = "myContent1\n" + boundary
  val contentJson = """{"key":"value"}"""
  val contentXml = """<something>some value</something>"""

  @Test @Ignore def testUploadbytes = {
    val request = new MockMultipartHttpServletRequest()

    request.setMethod("POST")
    request.setContentType("multipart/form-data; boundary=" + boundary)
    request.addHeader("Content-type", "multipart/form-data")
    request.addHeader("Content-length", content.getBytes.length.toString)
    request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
    request.addParameter("key","val")
    request.setRequestURI("/files/upload")

    val file = new MockMultipartFile("file", "/home/some/image.png", "application/png", content.getBytes)
    request.addFile(file)

    println(request.getFileMap)
    val postbody = new PostBodyRequest(request)
    val bytes = postbody.uploadedFile("file")
    assertNotNull(bytes)
    //    assertEquals(content,text)
  }

  @Test def testUploadText() {
    val request = new MockMultipartHttpServletRequest()
    request.setMethod("POST")
    request.addHeader("Content-length", content.getBytes.length.toString)
    request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
    request.setContent(content.getBytes)

    request.setRequestURI("/files/upload")
    val postbody = new PostBodyRequest(request)
    val text = postbody.asText
    assertNotNull(text)
    assertEquals(content, text)
  }

  @Test def testUploadJson() {
    val request = new MockMultipartHttpServletRequest()
    request.setMethod("POST")
    request.addHeader("Content-length", contentJson.getBytes.length.toString)
    request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
    request.setContent(contentJson.getBytes)

    request.setRequestURI("/files/upload")
    val postbody = new PostBodyRequest(request)
    val json = postbody.asJson
    assertNotNull(json)
    assertTrue(json.isInstanceOf[Map[_, _]])
  }

  @Test def testUploadXml() {
    val request = new MockMultipartHttpServletRequest()
    request.setMethod("POST")
    request.addHeader("Content-length", contentXml.getBytes.length.toString)
    request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
    request.setContent(contentXml.getBytes)

    request.setRequestURI("/files/upload")
    val postbody = new PostBodyRequest(request)
    val xml = postbody.asXml
    assertNotNull(xml)
    assertTrue(xml.isInstanceOf[Elem])
  }
}