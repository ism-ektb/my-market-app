package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.ItemWithQuantity;

public interface ItemWithQuantityRepo extends ReactiveCrudRepository<ItemWithQuantity, Long> {

    @Cacheable("iwq")
    Mono<ItemWithQuantity> findById(long id);

    @CachePut(value = "iwq", key = "#iwq.id", condition = "#iwq.id != null")
    Mono<ItemWithQuantity> save(ItemWithQuantity iwq);
}
