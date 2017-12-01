package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class MapHandler extends LambdaBase implements RequestStreamHandler  {

  private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    JSONObject event = parseEvent(inputStream);
    String encodedUml = getEncodedUml(event);
    try {
      String imageMap = plantUmlUtil.renderImageMap(encodedUml);
      String base64Response = Base64.getEncoder().encodeToString(imageMap.getBytes());
      SyntaxCheckResult syntaxCheckResult = plantUmlUtil.checkSyntax(encodedUml);
      if (!syntaxCheckResult.isError()) {
        sendOKDiagramResponse(outputStream, base64Response, DiagramType.IMAGEMAP);
      } else {
        sendDiagramResponse(outputStream, base64Response, DiagramType.IMAGEMAP, String.valueOf(HttpStatus.SC_UNPROCESSABLE_ENTITY));
      }
    } catch (StatusCodeException sce) {
      sendExceptionResponse(outputStream, sce);
    }
  }

}