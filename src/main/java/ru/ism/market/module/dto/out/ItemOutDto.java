package ru.ism.market.module.dto.out;

public record ItemOutDto(long id,
                         String title,
                         String description,
                         String imgPath,
                         long price,
                         int count) {
}
