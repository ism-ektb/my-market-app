package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ism.mymarketapp.module.Cart;

public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {
}
