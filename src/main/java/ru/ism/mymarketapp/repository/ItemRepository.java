package ru.ism.mymarketapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.ism.mymarketapp.module.Item;

public interface ItemRepository extends R2dbcRepository<Item, Long> {

    Flux<Item> findAllByTitleLikeIgnoreCase(String title, Pageable pageable);
}
