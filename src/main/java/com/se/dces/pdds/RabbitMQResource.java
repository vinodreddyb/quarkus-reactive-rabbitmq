package com.se.dces.pdds;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;
import jakarta.inject.Inject;

@Path("/rabbitmq")
public class RabbitMQResource {

    @Inject
    RabbitMQConsumer rabbitMQConsumer;

    @GET
    @Path("/stream1")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> streamMessagesFromQueue1() {
        return rabbitMQConsumer.getStream1();
    }


}