package fi.nitor.plantuml.lambda;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.servlet.utility.UmlExtractor;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Path("/")
public class PlantUmlController {

    static final Logger logger = Logger.getLogger(PlantUmlController.class);

    public static class Entity {
        public String imageData;

        Entity(String imageData) {
            this.imageData = imageData;
        }
    }

    @GET
    @Path("/img/{umlEncoded}")
    public Response imgEndpoint(@PathParam("umlEncoded") String uml) {
        return pngEndpoint(uml);
    }

    @GET
    @Path("/png/{umlEncoded}")
    public Response pngEndpoint(@PathParam("umlEncoded") String uml) {
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
        String imageData = Base64.getEncoder().encodeToString(baos.toByteArray());
        return Response.status(200)
                .entity(new Entity(imageData)).encoding("image/png")
                .build();
    }
}
