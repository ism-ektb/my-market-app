package ru.ism.mymarketapp.module.dto.out;

import java.util.List;

public record CartFullOutDto(
        List<ItemOutDto> items,
        long sum,
        boolean enoughMoneyToBuy,
        boolean payServiceIsAvailable
) {
}
