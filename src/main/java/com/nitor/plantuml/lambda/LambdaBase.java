package com.nitor.plantuml.lambda;

import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.nitor.plantuml.PlantUmlUtil.NOETAG;

class LambdaBase {

    private static final String ENV_VAR_KEY_STAGE = "stage";
    private static final String DEFAULT_STAGE = "dev";
    private static final String GRAPHVIZ_DOT = "GRAPHVIZ_DOT";
    static final String LAMBDA_TASK_ROOT = "LAMBDA_TASK_ROOT";
    private static final String DOT_PATH = "/opt/dot_static";
    static final long DEFAULT_MAX_AGE = 3600;

    private static final Logger logger = Logger.getLogger(LambdaBase.class);

    static {
        String stage = Optional.ofNullable(System.getenv(ENV_VAR_KEY_STAGE)).orElse(DEFAULT_STAGE);
        URL logPropsUrl = LambdaBase.class.getResource(String.format("/log4j-%s.properties", stage));
        if (logPropsUrl != null) {
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(logPropsUrl);
        }

        if (System.getenv(LAMBDA_TASK_ROOT) == null) {
            logger.error(String.format("%s environment variable is not set. Rendering without graphviz dot!", LAMBDA_TASK_ROOT));
        } else {
            System.setProperty(GRAPHVIZ_DOT, DOT_PATH);
        }
        logger.debug(String.format("GRAPHVIZ_DOT system property: %s", System.getProperty(GRAPHVIZ_DOT)));
    }

    Map<String, String> getCacheHeaders(String etag, long maxAge) {
        if (NOETAG.equals(etag)) {
            return Collections.emptyMap();
        } else {
            Map<String, String> headers = new HashMap<>();
            headers.put("ETag", etag);
            headers.put("Cache-Control", "public, max-age=" + maxAge);
            return headers;
        }
    }

    boolean isMatchingEtag(JSONObject event, String expectedEtag) {
        logger.debug(String.format("expected etag %s -> event: %s", expectedEtag, event.toJSONString()));
        return expectedEtag != null && !NOETAG.equals(expectedEtag) && getJSONObject(event, "headers")
                .entrySet().stream().anyMatch(pair ->
                        "if-none-match".equalsIgnoreCase(pair.getKey()) && expectedEtag.equals(pair.getValue()));
    }

    @SuppressWarnings("unchecked")
    void send304Response(OutputStream outputStream, Map<String, String> headers) throws IOException {
        JSONObject responseJson = new JSONObject();

        JSONObject headerJson = new JSONObject();
        headerJson.putAll(headers);
        headerJson.put("Access-Control-Allow-Origin", "*");

        responseJson.put("statusCode", "304");
        responseJson.put("headers", headerJson);

        internalSendResponse(outputStream, responseJson);
    }

    void sendOKDiagramResponse(OutputStream outputStream, String base64Response, DiagramType diagramType) throws IOException {
        sendOKDiagramResponse(outputStream, base64Response, diagramType, Collections.emptyMap());
    }

    void sendOKDiagramResponse(OutputStream outputStream, String base64Response,
                               DiagramType diagramType, Map<String, String> headers) throws IOException {
        sendDiagramResponse(outputStream, base64Response, diagramType, String.valueOf(HttpStatus.SC_OK), headers);
    }

    void sendDiagramResponse(OutputStream outputStream, String base64Response, DiagramType diagramType,
                             String statusCode) throws IOException {
        sendDiagramResponse(outputStream, base64Response, diagramType, statusCode, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    void sendDiagramResponse(OutputStream outputStream, String base64Response, DiagramType diagramType,
                             String statusCode, Map<String, String> headers) throws IOException {
        JSONObject responseJson = new JSONObject();

        JSONObject headerJson = new JSONObject();
        headerJson.putAll(headers);
        headerJson.put("Access-Control-Allow-Origin", "*");
        headerJson.put("Content-Type", diagramType.getMimeType());

        responseJson.put("statusCode", statusCode);
        responseJson.put("headers", headerJson);
        responseJson.put("body", base64Response);
        responseJson.put("isBase64Encoded", true);

        internalSendResponse(outputStream, responseJson);
    }

    void sendOKJSONResponse(OutputStream outputStream, String base64Response) throws IOException {
        sendOKJSONResponse(outputStream, base64Response, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    void sendOKJSONResponse(OutputStream outputStream, String base64Response, Map<String, String> headers) throws IOException {
        sendJSONResponse(outputStream, base64Response, String.valueOf(HttpStatus.SC_OK), headers);
    }

    void sendExceptionResponse(OutputStream outputStream, StatusCodeException statusCodeException) throws IOException {
        sendExceptionResponse(outputStream, statusCodeException, Collections.emptyMap());
    }

    void sendExceptionResponse(OutputStream outputStream, StatusCodeException statusCodeException,
                               Map<String, String> headers) throws IOException {
        String base64Response = Base64.getEncoder().encodeToString(statusCodeException.getMessage().getBytes());
        sendJSONResponse(outputStream, base64Response, statusCodeException.getStatusCode(), headers);
    }

    void sendJSONResponse(OutputStream outputStream, String base64Response, String statusCode) throws IOException {
        sendJSONResponse(outputStream, base64Response, statusCode, Collections.emptyMap());
    }

    void sendJSONResponse(OutputStream outputStream, String base64Response,
                          String statusCode, Map<String, String> headers) throws IOException {
        JSONObject responseJson = new JSONObject();

        JSONObject headerJson = new JSONObject();
        headerJson.putAll(headers);
        headerJson.put("Access-Control-Allow-Origin", "*");
        headerJson.put("Content-Type", "application/json");

        responseJson.put("statusCode", statusCode);
        responseJson.put("headers", headerJson);
        responseJson.put("body", base64Response);
        responseJson.put("isBase64Encoded", true);

        internalSendResponse(outputStream, responseJson);
    }

    void sendHTMLResponse(OutputStream outputStream, String htmlResponse, String statusCode) throws IOException {
        sendHTMLResponse(outputStream, htmlResponse, statusCode, Collections.emptyMap());
    }

    void sendHTMLResponse(OutputStream outputStream, String htmlResponse,
                          String statusCode, Map<String, String> headers) throws IOException {
        JSONObject responseJson = new JSONObject();

        JSONObject headerJson = new JSONObject();
        headerJson.putAll(headers);
        headerJson.put("Access-Control-Allow-Origin", "*");
        headerJson.put("Content-Type", "text/html");

        responseJson.put("statusCode", statusCode);
        responseJson.put("headers", headerJson);
        responseJson.put("body", htmlResponse);
        responseJson.put("isBase64Encoded", false);

        internalSendResponse(outputStream, responseJson);
    }

    void sendRedirectResponse(OutputStream outputStream, String redirectPath) throws IOException {
        sendRedirectResponse(outputStream, redirectPath, Collections.emptyMap());
    }

    void sendRedirectResponse(OutputStream outputStream, String redirectPath,
                              Map<String, String> headers) throws IOException {
        JSONObject responseJson = new JSONObject();

        JSONObject headerJson = new JSONObject();
        headerJson.putAll(headers);
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

    @SuppressWarnings("unchecked")
    Map<String, Object> getJSONObject(JSONObject parent, String key) {
        Object value = parent.get(key);
        Map<String, Object> map = new HashMap<>();
        if (value instanceof JSONObject) {
            map.putAll((JSONObject) value);
        }
        return map;
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