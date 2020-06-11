package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import com.nitor.plantuml.lambda.exception.BadRequestException;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class TxtHandler extends LambdaBase implements RequestStreamHandler  {

  private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    JSONObject event = parseEvent(inputStream);
    String encodedUml = getEncodedUml(event);
    try {
      ByteArrayOutputStream baos = plantUmlUtil.renderDiagram(encodedUml, DiagramType.TEXT_PLAIN);
      if (baos == null) {
        sendExceptionResponse(outputStream, new BadRequestException("Cannot generate the diagram"));
        return;
      }
      byte[] bytes = baos.toByteArray();
      String base64Response = Base64.getEncoder().encodeToString(bytes);
      sendOKDiagramResponse(outputStream, base64Response, DiagramType.TEXT_PLAIN);
    } catch (StatusCodeException sce) {
      sendExceptionResponse(outputStream, sce);
    }
  }

}