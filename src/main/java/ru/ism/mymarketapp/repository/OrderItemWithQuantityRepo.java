package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.ism.mymarketapp.module.OrderItemWithQuantity;

public interface OrderItemWithQuantityRepo extends ReactiveCrudRepository<OrderItemWithQuantity, Long> {
    Flux<OrderItemWithQuantity> findAllByOrderId(long orderId);
}
