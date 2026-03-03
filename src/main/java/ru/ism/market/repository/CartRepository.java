package ru.ism.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.market.module.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
