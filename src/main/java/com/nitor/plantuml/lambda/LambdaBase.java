package com.nitor.plantuml.lambda;

import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

class LambdaBase {

  private static final String ENV_VAR_KEY_STAGE = "stage";
  private static final String DEFAULT_STAGE = "dev";
  private static final String GRAPHVIZ_DOT = "GRAPHVIZ_DOT";
  static final String LAMBDA_TASK_ROOT = "LAMBDA_TASK_ROOT";
  private static final String DOT_PATH = "/opt/dot_static";
  private static final File LOCAL_FONT_CONFIG = new File(
          System.getenv(LAMBDA_TASK_ROOT) + "/fontconfig.properties");

  private static final Logger logger = Logger.getLogger(LambdaBase.class);

  static {
    String stage = Optional.ofNullable(System.getenv(ENV_VAR_KEY_STAGE)).orElse(DEFAULT_STAGE);
    LogManager.resetConfiguration();
    PropertyConfigurator.configure(String.format("log4j-%s.properties", stage));

    if (System.getenv(LAMBDA_TASK_ROOT) == null) {
      logger.error(String.format("%s environment variable is not set. Rendering without graphviz dot!", LAMBDA_TASK_ROOT));
    } else {
        System.setProperty(GRAPHVIZ_DOT, DOT_PATH);
    }
    logger.debug(String.format("GRAPHVIZ_DOT system property: %s", System.getProperty(GRAPHVIZ_DOT)));
    logger.debug(String.format("sun.awt.fontconfig=%s", System.getProperty("sun.awt.fontconfig")));
  }

  void sendOKDiagramResponse(OutputStream outputStream, String base64Response, DiagramType diagramType) throws IOException {
    sendDiagramResponse(outputStream, base64Response, diagramType, String.valueOf(HttpStatus.SC_OK));
  }

  @SuppressWarnings("unchecked")
  void sendDiagramResponse(OutputStream outputStream, String base64Response, DiagramType diagramType, String statusCode) throws IOException {
    JSONObject responseJson = new JSONObject();

    JSONObject headerJson = new JSONObject();
    headerJson.put("Access-Control-Allow-Origin", "*");
    headerJson.put("Content-Type", diagramType.getMimeType());
    headerJson.put("Cache-Control", "public, max-age=31536000"); //1 year max age

    responseJson.put("statusCode", statusCode);
    responseJson.put("headers", headerJson);
    responseJson.put("body", base64Response);
    responseJson.put("isBase64Encoded", true);

    internalSendResponse(outputStream, responseJson);
  }

  @SuppressWarnings("unchecked")
  void sendOKJSONResponse(OutputStream outputStream, String base64Response) throws IOException {
    sendJSONResponse(outputStream, base64Response, String.valueOf(HttpStatus.SC_OK));
  }

  void sendExceptionResponse(OutputStream outputStream, StatusCodeException statusCodeException) throws IOException {
    String base64Response = Base64.getEncoder().encodeToString(statusCodeException.getMessage().getBytes());
    sendJSONResponse(outputStream, base64Response, statusCodeException.getStatusCode());
  }

  void sendJSONResponse(OutputStream outputStream, String base64Response, String statusCode) throws IOException {
    JSONObject responseJson = new JSONObject();

    JSONObject headerJson = new JSONObject();
    headerJson.put("Access-Control-Allow-Origin", "*");
    headerJson.put("Content-Type", "application/json");

    responseJson.put("statusCode", statusCode);
    responseJson.put("headers", headerJson);
    responseJson.put("body", base64Response);
    responseJson.put("isBase64Encoded", true);

    internalSendResponse(outputStream, responseJson);
  }

  void sendHTMLResponse(OutputStream outputStream, String htmlResponse, String statusCode) throws IOException {
    JSONObject responseJson = new JSONObject();

    JSONObject headerJson = new JSONObject();
    headerJson.put("Access-Control-Allow-Origin", "*");
    headerJson.put("Content-Type", "text/html");

    responseJson.put("statusCode", statusCode);
    responseJson.put("headers", headerJson);
    responseJson.put("body", htmlResponse);
    responseJson.put("isBase64Encoded", false);

    internalSendResponse(outputStream, responseJson);
  }

  void sendRedirectResponse(OutputStream outputStream, String redirectPath) throws IOException {
    JSONObject responseJson = new JSONObject();

    JSONObject headerJson = new JSONObject();
    headerJson.put("Access-Control-Allow-Origin", "*");
    headerJson.put("Location", redirectPath);

    responseJson.put("statusCode", HttpStatus.SC_MOVED_PERMANENTLY);
    responseJson.put("headers", headerJson);
    responseJson.put("isBase64Encoded", false);

    internalSendResponse(outputStream, responseJson);
  }

  private void internalSendResponse(OutputStream outputStream, JSONObject responseJson) throws IOException {
    logger.debug(responseJson.toJSONString());
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toJSONString());
    writer.close();
  }

  String getEncodedUml(JSONObject event) throws IOException {
    if (event.get("pathParameters") != null) {
      JSONObject pps = (JSONObject) event.get("pathParameters");
      if (pps.get("encodedUml") == null) {
        handleInputError(null);
      }
      return (String) pps.get("encodedUml");
    }
    return null;
  }

  boolean isNitorStyle(JSONObject event) {
    JSONObject qsp;
    if ((event == null) || (qsp = (JSONObject) event.get("queryStringParameters")) == null) {
      return false;
    }
    return qsp.get("nitorStyle") != null;
  }

  JSONObject parseEvent(InputStream inputStream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    final JSONParser parser = new JSONParser();
    try {
      JSONObject event = (JSONObject) parser.parse(reader);
      logger.debug(event.toJSONString());
      return event;
    } catch (Exception e) {
      handleInputError(e);
    }
    return null;
  }

  private void handleInputError(Exception e) {
    throw new IllegalArgumentException("Could not parse parameters", e);
  }
}