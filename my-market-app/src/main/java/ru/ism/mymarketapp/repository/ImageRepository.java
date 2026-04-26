package ru.ism.mymarketapp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.Image;

public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {

    @Cacheable("images")
    Mono<Image> findByItemId(long item_id);
}
