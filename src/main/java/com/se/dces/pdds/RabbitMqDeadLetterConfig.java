package com.se.dces.pdds;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Identifier;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnector;
import io.smallrye.reactive.messaging.rabbitmq.internals.RabbitMQClientHelper;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class RabbitMqDeadLetterConfig {

    /*@Produces
    @Identifier("rabbitMQClient")
    RabbitMQClient rabbitMQClient( Vertx vertx) {
        RabbitMQOptions config = new RabbitMQOptions();
        config.setUri("amqp://guest:guest@localhost:5672");
        config.setVirtualHost("/");
        RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx, config);
        rabbitMQClient.addConnectionEstablishedCallback(promise -> {
            rabbitMQClient.exchangeDeclare("exchange-my-dlq", "fanout", true, false)
                    .compose(v -> {
                        var args = new JsonObject();
                        args.put("x-dead-letter-exchange", "exchange-my");
                        args.put("x-message-ttl", 5000);
                        return rabbitMQClient.queueDeclare("queue-my-dlq", true, false, false,args);
                    })
                    .compose(declareOk -> {
                        return rabbitMQClient.queueBind(declareOk.getQueue(), "exchange-my-dlq", "");
                    })
                    .onComplete(promise);

            rabbitMQClient.exchangeDeclare("exchange-my", "direct", true, false)
                    .compose(v -> {
                        var args = new JsonObject();
                        args.put("x-dead-letter-exchange", "exchange-my-dlq");
                        return rabbitMQClient.queueDeclare("queue-my", true, false, false,args);
                    })
                    .compose(declareOk -> {
                        return rabbitMQClient.queueBind(declareOk.getQueue(), "exchange-my", "key-my");
                    })
                    .onComplete(promise);

        });


    return rabbitMQClient;
    }*/

}
