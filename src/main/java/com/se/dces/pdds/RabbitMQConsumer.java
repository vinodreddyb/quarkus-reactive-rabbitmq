package com.se.dces.pdds;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class RabbitMQConsumer {

    private final Multi<String> stream1;
    private MultiEmitter<? super String> emitter1;
    public RabbitMQConsumer() {
        this.stream1 = Multi.createFrom().emitter(emitter -> this.emitter1 = emitter);

    }

    @Incoming("queue1")
    public CompletionStage<Void> consumeFromQueue1(Message<String> message) {
        emitter1.emit(message.getPayload());
        return message.ack();
    }

    public Multi<String> getStream1() {
        return stream1;
    }

}
