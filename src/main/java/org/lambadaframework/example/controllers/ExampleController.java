package org.lambadaframework.example.controllers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.lambadaframework.logger.LambdaLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/")
public class ExampleController {

    /**
     * LambdaLogger is a log4j wrapper for proper logging to Cloudwatch
     */
    static final Logger logger = LambdaLogger.getLogger(ExampleController.class, Level.DEBUG);

    @GET
    @Path("/")
    public Response indexEndpoint(
    ) {
        logger.debug("Request got");
        return Response.status(200)
                .entity("index")
                .build();
    }

    @GET
    @Path("{name}")
    public Response exampleEndpoint(
            @PathParam("name") String name
    ) {

        logger.debug("Request got");
        /**
         * You can read properties file using System.getProperty method.
         */
        logger.debug(System.getProperty("test"));
        return Response.status(200)
                .entity(name)
                .build();
    }

    @GET
    @Path("resource/{name}")
    public Response exampleSecondEndpoint(
            @PathParam("name") String name
    ) {

        logger.debug("Request got");
        return Response.status(201)
                .entity(name)
                .build();
    }
}
