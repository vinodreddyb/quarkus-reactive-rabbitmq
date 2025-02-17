package com.se.dces.pdds;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMessage;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class RabbitMQConsumer {



    private final RedisService redisService;

    public RabbitMQConsumer(RedisService redisService) {
        this.redisService = redisService;
    }


    //Properties:
    //content_type = text/plain   in rabbitmq ui while sending message
    //=
   /* @Incoming("queue-stream")
    public CompletionStage<Void> process(Message<JsonObject> quoteRequest)  {
        // simulate some hardworking task
        System.out.println("Received message: " + quoteRequest.getPayload().mapTo(Emp.class));

        var metadata = quoteRequest.getMetadata(IncomingRabbitMQMetadata.class);
        if(metadata.isPresent()) {
            var meta = metadata.get();
            var header = meta.getHeader("x-death", List.class);
            if(header.isPresent()) {
                final Optional<Map<String, Object>> rejected = header.get().stream()
                        .filter(x -> ((Map<String,Object>)x).get("reason").equals("rejected")).findFirst();
                if(rejected.isPresent()) {
                    var retryCount =  (Long) rejected.get().get("count");
                    System.out.println("Retry count " + 3);
                    if(retryCount > 3) {
                        System.out.println("Reject message");
                        return quoteRequest.ack();
                    }
                }
            }

            if(meta.getHeader("error", Boolean.class).orElse(false)) {
                System.out.println("Reject message");
                return quoteRequest.nack(new RuntimeException("Error scenario"));
            }
        }

        System.out.println("Process message");
        return quoteRequest.ack();
    }*/


    //@Incoming("queue-stream")
    public Uni<Void> process(IncomingRabbitMQMessage<JsonObject> quoteRequest)  {

        return Uni.createFrom().item(quoteRequest)
                .onItem()
                 .invoke(() ->{
                    System.out.println("Process message");

                     if (checkRetry(quoteRequest)) return;

                     var emp = quoteRequest.getPayload().mapTo(Emp.class);
                    if(emp.isError()) {
                        System.out.println("Reject message");
                        throw new RuntimeException("Error scenario");
                    }
                    quoteRequest.ack();
                })
                .onItem().ignore().andContinueWithNull()
                .onFailure().recoverWithUni(throwable -> {
                    quoteRequest.rejectMessage(throwable,false);
                    return Uni.createFrom().voidItem();
                });

    }

    @Incoming("queue-stream")
    public Uni<Void> consume(IncomingRabbitMQMessage<JsonObject> message) {
        if(checkRetry(message)) return Uni.createFrom().voidItem();
        return processMessage(message)
                .onFailure().recoverWithUni(ex -> handleFailure(message, ex));
    }



    private Uni<Void> processMessage(IncomingRabbitMQMessage<JsonObject> message) {
        System.out.println("Received message: " + message.getPayload());
        var payload = message.getPayload().mapTo(Emp.class);
        return redisService.setValue(payload.getName(),payload.getSalary());
    }

    private Uni<Void> handleFailure(IncomingRabbitMQMessage<JsonObject> message, Throwable ex) {
        System.err.println("Failed processing message: " + message + " | Error: " + ex.getMessage());
        message.rejectMessage(ex,false);
        return Uni.createFrom().voidItem();
    }

    private  boolean checkRetry(IncomingRabbitMQMessage<JsonObject> quoteRequest) {
        var metadata = quoteRequest.getMetadata(IncomingRabbitMQMetadata.class);
        if(metadata.isPresent()) {
            var meta = metadata.get();
            var header = meta.getHeader("x-death", List.class);
            if(header.isPresent()) {
                final Optional<Map<String, Object>> rejected = header.get().stream()
                        .filter(x -> ((Map<String,Object>)x).get("reason").toString().equals("rejected"))
                        .findAny();
                if(rejected.isPresent()) {
                    var retryCount =  (Long) rejected.get().get("count");
                    System.out.println("Retry count " + retryCount);
                    if(retryCount > 3) {
                        System.out.println("Retried more than allowed message");
                        quoteRequest.ack();
                        return true;
                    }
                }
            }

        }
        return false;
    }


}
