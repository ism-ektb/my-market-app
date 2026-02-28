package ru.ism.market.service;

import ru.ism.market.module.dto.out.CartOutDto;
import ru.ism.market.module.enums.Action;

public interface CartService {

    /**
     * Получить список товаров в корзине
     * @return
     */
    CartOutDto getCart();

    /**
     * Изменить число товаров с номером itemId в корзине
     * @param itemId
     * @param action
     * @return
     */
    CartOutDto changeItemsInCart(long itemId, Action action);
}
