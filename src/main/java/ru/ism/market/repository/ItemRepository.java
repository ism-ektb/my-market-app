package ru.ism.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.market.module.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemsByTitleLikeIgnoreCase(String title, Pageable pageable);
}
