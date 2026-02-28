package ru.ism.market.module.dto.out;

import java.util.List;

public record CartOutDto(
        List<ItemOutDto> items,
        long sum
) {
}
