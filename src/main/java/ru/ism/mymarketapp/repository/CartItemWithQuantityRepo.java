package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ism.mymarketapp.module.CartItemWithQuantity;

public interface CartItemWithQuantityRepo extends ReactiveCrudRepository<CartItemWithQuantity, Long> {
}
