package ru.ism.market.module.dto.out;

public record ItemShortOutDto(long id,
                              String title,
                              long price,
                              int count) {
}
