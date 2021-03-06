package com.xml.analyzer

import java.io.File

import scala.util.Try

import org.jsoup.nodes.{ Element, Node }

import com.agileengine.xml.JsoupFindByIdSnippet
import com.typesafe.scalalogging.LazyLogging

class AnalyzerTwoPages() extends LazyLogging {

  val buf = scala.collection.mutable.ArrayBuffer.empty[Node]

  def analyze(inpOriginPath: String, inpOtherPath: String): Try[Vector[Node]] = {

    Try {

      val inpOriginFile: File = new File(inpOriginPath)
      val inpOtherFile: File = new File(inpOtherPath)

      if (checkFile(inpOriginFile) && checkFile(inpOtherFile)) {

        buf.clear()
        JsoupFindByIdSnippet.findElementById(inpOriginFile, AnalyzerTwoPages.idElement).map(targetElement => {

          import org.jsoup.Jsoup
          import scala.collection.JavaConverters._

          Jsoup.parse(inpOtherFile, "UTF-8").body().childNodes().asScala.toList
            .foreach(n => analyzeElement(n, targetElement))

        })
      }

      buf.toVector
    }
  }

  private def checkFile(f: File): Boolean = {
    val res = f.exists() && f.canRead
    if (!res) logger.error(s"Error of reading of file ${f.getAbsolutePath}")
    res
  }

  private def analyzeElement(node: Node, origElement: Element): Unit = {

    //logger.debug(node.toString)
    if (checkSimilarity(node, origElement)) buf += node

    if (node.isInstanceOf[Element]) {
      val childs = node.childNodes()
      if (!childs.isEmpty) {
        (0 until childs.size()).foreach(i =>
          analyzeElement(node.childNode(i), origElement))
      }
    }

  }

  private def checkSimilarity(node: Node, origElement: Element): Boolean = {

    //    node.attr("class").contains(origElement.attr("class")) ||
    //      node.attr("title").contains(origElement.attr("title")) ||
    //      node.attr("href").contains(origElement.attr("href"))

    var i = 0
    val iter = origElement.attributes().iterator()
    while (iter.hasNext) {
      val attr = iter.next()
      if (node.attr(attr.getKey).contains(attr.getValue)) i = i + 1
    }
    i > 2
  }

}

object AnalyzerTwoPages {
  val idElement = "make-everything-ok-button"

  def apply() = new AnalyzerTwoPages()
}






