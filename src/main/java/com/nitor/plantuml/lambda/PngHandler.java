package com.nitor.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nitor.plantuml.PlantUmlUtil;
import com.nitor.plantuml.lambda.exception.StatusCodeException;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PngHandler extends LambdaBase implements RequestStreamHandler {
    private static final Logger logger = Logger.getLogger(PngHandler.class);
    private static final String TYPE_IDENTIFIER = "png";
    private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JSONObject event = parseEvent(inputStream);
        String encodedUml = getEncodedUml(event);
        final String etag = plantUmlUtil.getEtag(encodedUml, TYPE_IDENTIFIER);
        if (isMatchingEtag(event, etag)) {
            send304Response(outputStream, Collections.emptyMap());
            return;
        }

        try {
            SourceStringReader reader = plantUmlUtil.readDiagram(encodedUml);
            ByteArrayOutputStream baos = plantUmlUtil.renderDiagram(reader, DiagramType.IMAGE_PNG);
            if (isNitorStyle(event)) {
                baos = applyBackground(baos);
            }
            byte[] bytes = baos.toByteArray();
            String base64Response = Base64.getEncoder().encodeToString(bytes);
            SyntaxCheckResult syntaxCheckResult = plantUmlUtil.checkSyntax(encodedUml);
            if (!syntaxCheckResult.isError()) {
                sendOKDiagramResponse(outputStream, base64Response, DiagramType.IMAGE_PNG,
                        getCacheHeaders(etag, DEFAULT_MAX_AGE));
            } else {
                sendDiagramResponse(outputStream, base64Response, DiagramType.IMAGE_PNG,
                        String.valueOf(HttpStatus.SC_UNPROCESSABLE_ENTITY));
            }
        } catch (StatusCodeException sce) {
            sendExceptionResponse(outputStream, sce);
        }
    }

    private ByteArrayOutputStream applyBackground(ByteArrayOutputStream diagramImage) {
        byte[] originalDiagramBytes = diagramImage.toByteArray();
        try {
            if (!Files.exists(Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.png"))) {
                logger.error("Background image not found!");
                return bytesToByteArrayOutputStream(originalDiagramBytes);
            }
            Path pathToTmp = Paths.get("/tmp");
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx------");
            Path diagramImageFile = Files.createTempFile(pathToTmp, null, null);
            Files.write(diagramImageFile, originalDiagramBytes);
            Path diagramWithBackgroundFile = Files.createTempFile(Paths.get("/tmp"), null, null);
            Path tempBackgroundFile = Files.createTempFile(Paths.get("/tmp"), null, null);
            Path bgFile = Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.png");
            Path script = Paths.get(System.getenv(LAMBDA_TASK_ROOT), "bg.sh");

            Process p = new ProcessBuilder("/bin/sh", "-x", script.toString(), diagramImageFile.toString(),
                    bgFile.toString(), tempBackgroundFile.toString(), diagramWithBackgroundFile.toString())
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            p.waitFor(5, TimeUnit.SECONDS);
            if (p.exitValue() == 0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(Files.readAllBytes(diagramWithBackgroundFile));
                return baos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.error(String.format("Problem with background apply"));

        return bytesToByteArrayOutputStream(originalDiagramBytes);
    }

    private ByteArrayOutputStream bytesToByteArrayOutputStream(byte[] bytes) {
        ByteArrayOutputStream originalImageData = new ByteArrayOutputStream();
        try {
            originalImageData.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return originalImageData;
    }

}