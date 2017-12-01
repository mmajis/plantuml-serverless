package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class SyntaxHandler extends LambdaBase implements RequestStreamHandler  {

  private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    try {
      JSONObject event = parseEvent(inputStream);
      String encodedUml = getEncodedUml(event);
      SyntaxCheckResult syntaxCheckResult = plantUmlUtil.checkSyntax(encodedUml);
      Gson gson = new GsonBuilder().create();
      String json = gson.toJson(syntaxCheckResult);
      String base64Response = Base64.getEncoder().encodeToString(json.getBytes());
      if (!syntaxCheckResult.isError()) {
        sendOKJSONResponse(outputStream, base64Response);
      } else {
        sendJSONResponse(outputStream, base64Response, String.valueOf(HttpStatus.SC_UNPROCESSABLE_ENTITY));
      }
    } catch (StatusCodeException sce) {
      sendExceptionResponse(outputStream, sce);
    }
  }

}