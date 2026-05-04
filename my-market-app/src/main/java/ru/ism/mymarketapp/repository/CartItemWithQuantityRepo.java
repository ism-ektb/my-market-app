package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.CartItemWithQuantity;

public interface CartItemWithQuantityRepo extends ReactiveCrudRepository<CartItemWithQuantity, Long> {

    Flux<CartItemWithQuantity> findByCartId(long id);
}
