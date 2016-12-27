package com.nitor.plantuml;

import com.nitor.plantuml.lambda.DiagramType;
import net.sourceforge.plantuml.FileFormat;
import org.apache.log4j.Logger;

class DiagramTypeUtil {

  private static Logger logger = Logger.getLogger(DiagramTypeUtil.class);

  static FileFormat asFileFormat(DiagramType diagramType) {
    switch (diagramType) {
      case IMAGE_PNG:
        return FileFormat.PNG;
      case IMAGE_SVG_XML:
        return FileFormat.SVG;
      case TEXT_PLAIN:
        return FileFormat.UTXT;
      default:
        logger.debug("Defaulting to PNG diagram");
        return FileFormat.PNG;
    }
  }
}
