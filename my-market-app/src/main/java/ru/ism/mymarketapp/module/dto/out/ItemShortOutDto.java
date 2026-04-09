package ru.ism.mymarketapp.module.dto.out;

public record ItemShortOutDto(long id,
                              String title,
                              long price,
                              int count) {
}
