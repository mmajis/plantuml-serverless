package com.nitor.plantuml.lambda.png;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.Optional;

public class PngHandler implements RequestStreamHandler {

  private static final String ENV_VAR_KEY_STAGE = "stage";
  private static final String DEFAULT_STAGE = "dev";
  private static final String GRAPHVIZ_DOT = "GRAPHVIZ_DOT";
  private static final String LAMBDA_TASK_ROOT = "LAMBDA_TASK_ROOT";
  private static final String DOT_PATH = "/tmp/dot_static";

  private static final Logger logger = Logger.getLogger(PngHandler.class);

  private JSONParser parser = new JSONParser();


  static {
    String stage = Optional.ofNullable(System.getenv(ENV_VAR_KEY_STAGE)).orElse(DEFAULT_STAGE);
    LogManager.resetConfiguration();
    PropertyConfigurator.configure(String.format("log4j-%s.properties", stage));

    if (System.getenv(LAMBDA_TASK_ROOT) == null) {
      logger.error(String.format("%s environment variable is not set. Rendering without graphviz dot!", LAMBDA_TASK_ROOT));
    } else {
      String taskRootDotPath = String.format("%s/dot_static", System.getenv(LAMBDA_TASK_ROOT));
      try {
        File dotFile = new File(DOT_PATH);
        Files.copy(new File(taskRootDotPath).toPath(), dotFile.toPath());
        Files.setPosixFilePermissions(dotFile.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
        System.setProperty(GRAPHVIZ_DOT, DOT_PATH);
      } catch (IOException e) {
        logger.error(String.format("Failed to copy graphviz dot executable to %s. Rendering without graphviz dot!", DOT_PATH), e);
      }
    }
    logger.debug(String.format("GRAPHVIZ_DOT system property: %s", System.getProperty(GRAPHVIZ_DOT)));
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();
    String encodedUml = "";
    String responseCode = "200";

    try {
      JSONObject event = (JSONObject) parser.parse(reader);
      logger.debug(event.toJSONString());
      if (event.get("pathParameters") != null) {
        logger.debug(((JSONObject)event.get("pathParameters")).toJSONString());
        JSONObject pps = (JSONObject) event.get("pathParameters");
        if (pps.get("encodedUml") != null) {
          encodedUml = (String) pps.get("encodedUml");
        }
      }
      JSONObject headerJson = new JSONObject();
      headerJson.put("Access-Control-Allow-Origin", "*");
      headerJson.put("Content-Type", "image/png");

      ByteArrayOutputStream baos = handleUml(encodedUml);
      byte[] bytes = baos.toByteArray();
      String base64Response = Base64.getEncoder().encodeToString(bytes);

      responseJson.put("statusCode", responseCode);
      responseJson.put("headers", headerJson);
      responseJson.put("body", base64Response);
      responseJson.put("isBase64Encoded", true);
    } catch (Exception ex) {
      responseJson.put("statusCode", "400");
      responseJson.put("exception", ex);
    }
    logger.debug(responseJson.toJSONString());
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toJSONString());
    writer.close();
  }

  private ByteArrayOutputStream handleUml(String uml) {
    logger.debug(String.format("Got encoded uml: %s", uml));
    uml = UmlExtractor.getUmlSource(uml);
    logger.debug(String.format("Decoded uml: %s", uml));
    SourceStringReader reader = new SourceStringReader(uml);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        reader.generateImage(baos, new FileFormatOption(FileFormat.PNG, false));
    } catch (IOException e) {
        logger.error(e);
    }
    return baos;
  }

}