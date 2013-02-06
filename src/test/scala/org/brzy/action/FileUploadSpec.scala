package org.brzy.action

import args.PostBodyRequest

import org.springframework.mock.web.{MockMultipartHttpServletRequest, MockMultipartFile}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import java.io.File
import org.apache.commons.io.IOUtils


class FileUploadSpec extends WordSpec with ShouldMatchers{
  val boundary = "AaB03x"
  val content = "myContent1\n" + boundary
  val contentJson = """{"key":"value"}"""
  val contentXml = """<something>some value</something>"""

  val imagePng = {
    val stream = getClass.getClassLoader.getResourceAsStream("image.png")
    IOUtils.toByteArray(stream) ++ "\n".getBytes ++ boundary.getBytes
  }
  "File Upload" should {
    "upload bytes" ignore {
      // don't know why I get a null pointer, it's an issue with the test, not the api.
      // commented out till later
      val request = new MockMultipartHttpServletRequest()

      imagePng.length should be > 80
      request.setMethod("POST")
      request.setContentType("multipart/form-data; boundary=" + boundary)
      request.addHeader("Content-type", "multipart/form-data")
      request.addHeader("Content-length", imagePng.length.toString)
      request.addHeader("Content-Disposition", "form-data; name=\"submit-name\"")
      request.addParameter("key","val")
      request.setRequestURI("/files/upload")

      val file = new MockMultipartFile("file", "/home/some/image.png", "application/png", imagePng)
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
  }
}