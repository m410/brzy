package org.brzy.action

import args.PostBodyRequest
import org.scalatest.junit.JUnitSuite

import java.io.FileInputStream
import xml.Elem
import org.springframework.mock.web.{MockHttpServletRequest, MockMultipartHttpServletRequest, MockMultipartFile, MockHttpServletResponse}
import io.Source
import org.junit.{Ignore, Test}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers


class FileUploadSpec extends WordSpec with ShouldMatchers{
  val boundary = "AaB03x"
  val content = "myContent1\n" + boundary
  val contentJson = """{"key":"value"}"""
  val contentXml = """<something>some value</something>"""

  "File Upload" should {
    "upload bytes" in {
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
      val bytes = postbody.paramAsFile("file")
      assert(bytes != null)
    }
    "upload text" in {
      val request = new MockMultipartHttpServletRequest()
      request.setMethod("POST")
      request.addHeader("Content-length", content.getBytes.length.toString)
      request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
      request.setContent(content.getBytes)

      request.setRequestURI("/files/upload")
      val postbody = new PostBodyRequest(request)
      val text = postbody.asText
      assert(text != null)
      assert(content.equalsIgnoreCase(text))
    }
    "upload json" in {
      val request = new MockMultipartHttpServletRequest()
      request.setMethod("POST")
      request.addHeader("Content-length", contentJson.getBytes.length.toString)
      request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
      request.setContent(contentJson.getBytes)

      request.setRequestURI("/files/upload")
      val postbody = new PostBodyRequest(request)
      val json = postbody.asJson
      assert(json != null)
      assert(true == json.isInstanceOf[Map[_, _]])
    }
    "upload xml" in {
      val request = new MockMultipartHttpServletRequest()
      request.setMethod("POST")
      request.addHeader("Content-length", contentXml.getBytes.length.toString)
      request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
      request.setContent(contentXml.getBytes)

      request.setRequestURI("/files/upload")
      val postbody = new PostBodyRequest(request)
      val xml = postbody.asXml
      assert(xml != null)
      assert(true == xml.isInstanceOf[Elem])
    }
  }


}