package ru.ism.mymarketapp.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.Image;
import ru.ism.mymarketapp.module.ItemWithQuantity;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer itemsCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "items",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(3, ChronoUnit.MINUTES))

        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer imageCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "images",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(3, ChronoUnit.MINUTES))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(Image.class)
                                )
                        )
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer iwqCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "iwq",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(3, ChronoUnit.MINUTES))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ItemWithQuantity.class)
                                )
                        )
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer ciwqCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "ciwq",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(3, ChronoUnit.MINUTES))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(CartItemWithQuantity.class)
                                )
                        )
        );
    }
}
