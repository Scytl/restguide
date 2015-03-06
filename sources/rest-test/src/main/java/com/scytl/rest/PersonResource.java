package com.scytl.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/person")
public class PersonResource {

    private String OUTPUT = "{\n" +
            "  \"firstName\": \"John\",\n" +
            "  \"lastName\": \"Smith\",\n" +
            "  \"isAlive\": true,\n" +
            "  \"age\": 25,\n" +
            "  \"height_cm\": 167.6,\n" +
            "  \"address\": {\n" +
            "    \"streetAddress\": \"21 2nd Street\",\n" +
            "    \"city\": \"New York\",\n" +
            "    \"state\": \"NY\",\n" +
            "    \"postalCode\": \"10021-3100\"\n" +
            "  },\n" +
            "  \"phoneNumbers\": [\n" +
            "    {\n" +
            "      \"type\": \"home\",\n" +
            "      \"number\": \"212 555-1234\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"office\",\n" +
            "      \"number\": \"646 555-4567\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"children\": [],\n" +
            "  \"spouse\": null\n" +
            "}";

    @GET
    @Path("{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response person(@PathParam("personId") String bookId) {
       return Response.ok(OUTPUT).build();
    }

}
