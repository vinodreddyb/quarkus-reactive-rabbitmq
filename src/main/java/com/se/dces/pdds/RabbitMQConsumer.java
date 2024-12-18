package com.se.dces.pdds;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class RabbitMQConsumer {





    //Properties:
    //content_type = text/plain   in rabbitmq ui while sending message
    //=
    @Incoming("queue1")
    @Outgoing("quotes")
    @Blocking
    @RunOnVirtualThread
    public String process(Message<String> quoteRequest) throws InterruptedException {
        // simulate some hard working task
        System.out.println("Received message: " + quoteRequest.getPayload());
      //  Thread.sleep(200);
        return quoteRequest.getPayload();
    }



}
