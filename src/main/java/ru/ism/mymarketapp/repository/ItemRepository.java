package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ism.mymarketapp.module.Item;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
}
