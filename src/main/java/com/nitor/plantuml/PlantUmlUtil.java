package com.nitor.plantuml;

import com.nitor.plantuml.lambda.DiagramType;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PlantUmlUtil {

  private static final Logger logger = Logger.getLogger(PlantUmlUtil.class);

  public ByteArrayOutputStream renderDiagram(String uml, DiagramType diagramType) throws IOException {
    logger.debug(String.format("Got encoded uml: %s", uml));
    uml = UmlExtractor.getUmlSource(uml);
    logger.debug(String.format("Decoded uml: %s", uml));
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    reader.generateImage(baos, new FileFormatOption(DiagramTypeUtil.asFileFormat(diagramType), true));
    return baos;
  }

  public String renderImageMap(String uml) throws IOException {
    logger.debug(String.format("Got encoded uml: %s", uml));
    uml = UmlExtractor.getUmlSource(uml);
    logger.debug(String.format("Decoded uml: %s", uml));
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    return reader.generateImage(baos, new FileFormatOption(FileFormat.PNG, true));
  }
}
