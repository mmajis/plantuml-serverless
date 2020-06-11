package com.nitor.plantuml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitor.plantuml.lambda.DiagramType;
import com.nitor.plantuml.lambda.SyntaxCheckResult;
import com.nitor.plantuml.lambda.exception.BadRequestException;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import net.sourceforge.plantuml.syntax.SyntaxChecker;
import net.sourceforge.plantuml.syntax.SyntaxResult;
import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.error.PSystemError;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PlantUmlUtil {

  private static final Logger logger = Logger.getLogger(PlantUmlUtil.class);
  public static final String DIAGRAM_TYPE_UNKNOWN = "UNKNOWN";

  public ByteArrayOutputStream renderDiagram(String encodedUml, DiagramType diagramType) throws IOException {
    String uml = decodeUml(encodedUml);
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final BlockUml blockUml = reader.getBlocks().get(0);
    final Diagram diagram = blockUml.getDiagram();
    if (diagram instanceof PSystemError) {
      return null;
    }
    diagram.exportDiagram(baos, 0, new FileFormatOption(DiagramTypeUtil.asFileFormat(diagramType), true));
    return baos;
  }

  public String renderImageMap(String encodedUml) throws IOException {
    String uml = decodeUml(encodedUml);
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    return reader.generateImage(baos, new FileFormatOption(FileFormat.PNG, true));
  }

  public SyntaxCheckResult checkSyntax(String encodedUml) throws IOException {
    String uml = decodeUml(encodedUml);
    SyntaxResult syntaxResult = SyntaxChecker.checkSyntax(uml);
    if (logger.isDebugEnabled()) {
      Gson gson = new GsonBuilder().create();
      String json = gson.toJson(syntaxResult);
      logger.debug(json);
    }
    String diagramType = syntaxResult.getUmlDiagramType() != null ? syntaxResult.getUmlDiagramType().name() : DIAGRAM_TYPE_UNKNOWN;
    LineLocation lineLoc = syntaxResult.getLineLocation();
    int lineLocationPos = -1;
    if (lineLoc != null) {
      lineLocationPos = lineLoc.getPosition();
    }
    SyntaxCheckResult result = new SyntaxCheckResult(syntaxResult.isError(), diagramType,
        String.valueOf(lineLocationPos), new ArrayList<String>(syntaxResult.getErrors()));
    return result;
  }

  private String decodeUml(String encodedUml) {
    logger.debug(String.format("Got encoded uml: %s", encodedUml));
    try {
      String decodedUml = UmlExtractor.getUmlSource(encodedUml);
      logger.debug(String.format("Decoded uml: %s", decodedUml));
      return decodedUml;
    } catch (IllegalArgumentException iae) {
      SyntaxCheckResult result = new SyntaxCheckResult(true, DIAGRAM_TYPE_UNKNOWN, "0",
          Arrays.asList(String.format("Could not decode UML from request path: %s", encodedUml)));
      Gson gson = new GsonBuilder().create();
      String json = gson.toJson(result);
      throw new BadRequestException(json, iae);
    }
  }

}
