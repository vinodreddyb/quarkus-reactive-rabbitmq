package com.se.dces.pdds;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisService {

    private ReactiveKeyCommands<String> keyCommands;
    private ReactiveValueCommands<String, Long> countCommands;

    public RedisService(ReactiveRedisDataSource redis) {

        countCommands = redis.value(Long.class);
        keyCommands = redis.key();
    }

    public Uni<Void> setValue(String key, Long value) {
        return countCommands.set(key, value);
    }

    public Uni<Long> getValue(String key) {

        return countCommands.get(key);
    }
}