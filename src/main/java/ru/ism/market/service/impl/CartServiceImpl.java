package ru.ism.market.service.impl;

import org.springframework.stereotype.Service;
import ru.ism.market.module.dto.out.CartOutDto;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.enums.Action;
import ru.ism.market.service.CartService;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    /**
     * Получить список товаров в корзине
     *
     * @return
     */
    @Override
    public CartOutDto getCart() {
        return new CartOutDto(List.of(new ItemOutDto(1, "name", "desc", "images/1.jpg", 10, 2)), 10L);
    }

    /**
     * Изменить число товаров с номером itemId в корзине
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public CartOutDto changeItemsInCart(long itemId, Action action) {
        return new CartOutDto(List.of(new ItemOutDto(1, "name", "desc", "images/1.jpg", 10, 3)), 15L);

    }
}
