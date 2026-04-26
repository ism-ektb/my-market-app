package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ism.mymarketapp.module.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
