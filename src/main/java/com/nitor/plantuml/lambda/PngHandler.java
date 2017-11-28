package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class PngHandler extends LambdaBase implements RequestStreamHandler  {

  private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();
  private static final Logger logger = Logger.getLogger(PngHandler.class);


  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    String encodedUml = getEncodedUml(inputStream);
    try {
      ByteArrayOutputStream baos = plantUmlUtil.renderDiagram(encodedUml, DiagramType.IMAGE_PNG);
      //TODO don't 
      byte[] bytes = baos.toByteArray();
      String base64Response = Base64.getEncoder().encodeToString(bytes);
      SyntaxCheckResult syntaxCheckResult = plantUmlUtil.checkSyntax(encodedUml);
      if (!syntaxCheckResult.isError()) {
        sendOKDiagramResponse(outputStream, base64Response, DiagramType.IMAGE_PNG);
      } else {
        sendDiagramResponse(outputStream, base64Response, DiagramType.IMAGE_PNG, String.valueOf(HttpStatus.SC_UNPROCESSABLE_ENTITY));
      }
    } catch (StatusCodeException sce) {
      sendExceptionResponse(outputStream, sce);
    }
  }

  private ByteArrayOutputStream applyBackground(ByteArrayOutputStream diagramImage) {
    try {
      if (!Files.exists(Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.png"))) {
        logger.error("Background image not found!");
        return diagramImage;
      }
      Path diagramImageFile = Files.createTempFile(Paths.get("/tmp"), null, null, null);
      Files.write(diagramImageFile, diagramImage.toByteArray());
      Path diagramWithBackgroundFile = Files.createTempFile(Paths.get("/tmp"), null, null, null);
      Path bgFile = Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.png");
      Path script = Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.sh");
      Process p = new ProcessBuilder(script.toString(), diagramImageFile.toString(),
          bgFile.toString(), diagramWithBackgroundFile.toString()).start();
      if (p.exitValue() == 0) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Files.readAllBytes(diagramWithBackgroundFile));
        return baos;
      } else {
        logger.error(String.format("Problem with background apply"));
        return diagramImage;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return diagramImage;
    }
  }

}