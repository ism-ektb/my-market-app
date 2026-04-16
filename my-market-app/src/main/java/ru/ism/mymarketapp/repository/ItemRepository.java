package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.Item;

public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Cacheable("items")
    Mono<Item> findById(long id);

    @Cacheable(value = "items", key = "#title + '_' + #pageable")
    Flux<Item> findAllByTitleLikeIgnoreCase(String title, Pageable pageable);
}
