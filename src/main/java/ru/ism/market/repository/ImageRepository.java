package ru.ism.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.market.module.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
