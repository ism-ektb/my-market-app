package ru.ism.market.module.dto.out;

import java.util.List;

public record OrderOutDto(long id,
                          List<ItemShortOutDto> items,
                          long totalSum) {
}
