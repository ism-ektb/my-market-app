package ru.ism.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.market.module.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
