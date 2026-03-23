package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ism.mymarketapp.module.ItemWithQuantity;

public interface ItemWithQuantityRepo extends ReactiveCrudRepository<ItemWithQuantity, Long> {
}
