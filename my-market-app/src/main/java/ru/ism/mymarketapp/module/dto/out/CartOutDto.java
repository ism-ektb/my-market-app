package ru.ism.mymarketapp.module.dto.out;

import java.util.List;

public record CartOutDto(
        List<ItemOutDto> items,
        long sum
) {
}
