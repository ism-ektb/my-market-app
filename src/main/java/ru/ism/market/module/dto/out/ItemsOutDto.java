package ru.ism.market.module.dto.out;

import java.util.List;

public record ItemsOutDto(List<List<ItemOutDto>> items,
                          Paging paging) {
}
