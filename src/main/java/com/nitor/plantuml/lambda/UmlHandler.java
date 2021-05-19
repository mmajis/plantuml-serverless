package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class UmlHandler extends LambdaBase implements RequestStreamHandler {
    private static final String TYPE_IDENTIFIER = "uml";
    private static final String CONTENT_CLASSPATH = "/ui/umlsource.html";
    private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

    @Override
    public void handleRequest(final InputStream inputStream,
                              final OutputStream outputStream,
                              final Context context) throws IOException {
        JSONObject event = parseEvent(inputStream);
        String encodedUml = Optional.ofNullable(getEncodedUml(event)).orElse("");
        final String etag = plantUmlUtil.getEtag(encodedUml, TYPE_IDENTIFIER);
        if (isMatchingEtag(event, etag)) {
            send304Response(outputStream, Collections.emptyMap());
            return;
        }

        String decodedUml = plantUmlUtil.decodeUml(encodedUml)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        String editorUrl="../../";
        try(InputStream content = this.getClass().getResourceAsStream(CONTENT_CLASSPATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(content))) {
            StringBuffer sb = new StringBuffer();
            Stream<String> stream = br.lines();
            stream.forEach(l -> sb.append(l.replace("{{uml_source}}", decodedUml)
                    .replace("{{editor_url}}", editorUrl)));
            sendHTMLResponse(outputStream, sb.toString(), String.valueOf(HttpStatus.SC_OK),
                    getCacheHeaders(etag, DEFAULT_MAX_AGE));
        } catch (StatusCodeException sce) {
            sendExceptionResponse(outputStream, sce);
        }
    }
}
