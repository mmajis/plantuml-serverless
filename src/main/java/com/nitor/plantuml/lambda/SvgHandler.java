package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class SvgHandler extends LambdaBase implements RequestStreamHandler  {

  private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    String encodedUml = getEncodedUml(inputStream);
    try {
      ByteArrayOutputStream baos = plantUmlUtil.renderDiagram(encodedUml, DiagramType.IMAGE_SVG_XML);
      byte[] bytes = baos.toByteArray();
      String base64Response = Base64.getEncoder().encodeToString(bytes);
      sendOKResponse(outputStream, base64Response, DiagramType.IMAGE_SVG_XML);
    } catch (Exception e) {
      sendErrorResponse(outputStream, e);
    }
  }

}