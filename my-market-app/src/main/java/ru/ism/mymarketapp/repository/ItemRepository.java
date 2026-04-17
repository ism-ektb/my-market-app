package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.Item;

@Transactional
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Cacheable("items")
    Mono<Item> findById(long id);

    @Cacheable(value = "items", key = "#title + '_' + #pageable")
    Flux<Item> findAllByTitleLikeIgnoreCase(String title, Pageable pageable);

    @CacheEvict(value = "items", allEntries = true)
    Mono<Item> save(Item item);
}
