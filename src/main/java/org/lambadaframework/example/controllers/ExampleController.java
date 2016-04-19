package org.lambadaframework.example.controllers;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/")
public class ExampleController {


    static final Logger logger = Logger.getLogger(ExampleController.class);

    static class Entity {
        public int id = 1;
        public String name;

        public Entity(String name) {
            this.name = name;
        }
    }

    @GET
    @Path("/")
    public Response indexEndpoint(
    ) {
        logger.debug("Request got");
        return Response.status(200)
                .entity(new Entity("John doe"))
                .build();
    }

    @GET
    @Path("{name}")
    public Response exampleEndpoint(
            @PathParam("name") String name
    ) {

        logger.debug("Request got");
        return Response.status(200)
                .entity(new Entity(name))
                .build();
    }

    @GET
    @Path("resource/{name}")
    public Response exampleSecondEndpoint(
            @PathParam("name") String name
    ) {

        logger.debug("Request got");
        return Response.status(201)
                .entity(new Entity(name))
                .build();
    }
}
