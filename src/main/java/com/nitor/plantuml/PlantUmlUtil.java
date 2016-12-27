package com.nitor.plantuml;

import com.nitor.plantuml.lambda.DiagramType;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PlantUmlUtil {

  private static final Logger logger = Logger.getLogger(PlantUmlUtil.class);

  public ByteArrayOutputStream renderDiagram(String uml, DiagramType diagramType) {
    logger.debug(String.format("Got encoded uml: %s", uml));
    uml = UmlExtractor.getUmlSource(uml);
    logger.debug(String.format("Decoded uml: %s", uml));
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      reader.generateImage(baos, new FileFormatOption(DiagramTypeUtil.asFileFormat(diagramType), true));
    } catch (IOException e) {
      logger.error(e);
    }
    return baos;
  }
}
