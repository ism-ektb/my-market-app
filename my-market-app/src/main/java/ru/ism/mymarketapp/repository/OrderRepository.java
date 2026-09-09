package ru.ism.mymarketapp.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.ism.mymarketapp.module.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findAllByUserId(Long userId);
}
