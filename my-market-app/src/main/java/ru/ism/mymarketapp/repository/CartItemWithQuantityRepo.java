package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.CartItemWithQuantity;

public interface CartItemWithQuantityRepo extends ReactiveCrudRepository<CartItemWithQuantity, Long> {

    @Cacheable(value = "ciwq", key = "#id")
    Mono<CartItemWithQuantity> findByItemId(long id);

    @CachePut(value = "ciwq", key = "#ciwq.itemId")
    Mono<CartItemWithQuantity> save(CartItemWithQuantity ciwq);
}
