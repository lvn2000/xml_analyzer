package com.xml.analyzer

import scala.util.{ Failure, Success }

import com.typesafe.scalalogging.LazyLogging

object Analyzer extends App with LazyLogging {

  if (args.length < 2) {
    logger.error("Need to provide 2 arguments: <input_origin_file_path> and <input_other_sample_file_path>")
  } else {

    AnalyzerTwoPages().analyze(args(0), args(1)) match {
      case Success(v)  => logger.info(s"Found similar elements (${v.length}): \n  ${v.mkString("\n")}")
      case Failure(ex) => logger.error(s"Error during working of program ${ex.getMessage}")
    }

  }

}