package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.CartItemWithQuantity;

public interface CartItemWithQuantityRepo extends ReactiveCrudRepository<CartItemWithQuantity, Long> {

    @Cacheable(value = "ciwq", key = "#id", unless = "#result == null")
    Mono<CartItemWithQuantity> findByItemId(long id);

    @CachePut(value = "ciwq", key = "#ciwq.itemId", condition = "#ciwq.cart_id != null")
    Mono<CartItemWithQuantity> save(CartItemWithQuantity ciwq);

    @CacheEvict(value = "ciwq", allEntries = true)
    Mono<Void> deleteAll();
}
