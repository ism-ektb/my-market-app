package ru.ism.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.market.module.ItemWithQuantity;

public interface ItemWithQuantityRepo extends JpaRepository<ItemWithQuantity, Long> {
}
