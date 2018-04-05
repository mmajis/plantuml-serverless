package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.stream.Stream;

public class UIHandler extends LambdaBase implements RequestStreamHandler  {

  private static final Logger logger = Logger.getLogger(UIHandler.class);
  private static final String CONTENT_CLASSPATH = "/ui/index.html";

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    JSONObject event = parseEvent(inputStream);
    String path = (String)((JSONObject)event.get("requestContext")).get("path");
    logger.debug("Path: " + path);
    if (path != null && !path.endsWith("/")) {
      sendRedirectResponse(outputStream, path + "/");
    }

    InputStream content = this.getClass().getResourceAsStream(CONTENT_CLASSPATH);
    BufferedReader br = new BufferedReader(new InputStreamReader((content)));
    StringBuffer sb = new StringBuffer();

    Stream<String> stream = br.lines();
    stream.forEach(l -> sb.append(l));

    sendHTMLResponse(outputStream, sb.toString(), String.valueOf(HttpStatus.SC_OK));
    content.close();
  }
}