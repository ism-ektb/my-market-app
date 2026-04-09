package ru.ism.mymarketapp.module.dto.out;

import java.util.List;

public record ItemsOutDto(List<List<ItemOutDto>> items,
                          Paging paging) {
}
