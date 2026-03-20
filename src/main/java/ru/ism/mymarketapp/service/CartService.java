package ru.ism.mymarketapp.service;

import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.module.enums.Action;

public interface CartService {
    /**
     * Получить список товаров в корзине
     * @return
     */
    Mono<CartOutDto> getItemInCart();

    /**
     * Изменить число товаров с номером itemId в корзине
     * @param itemId
     * @param action
     * @return
     */
    Mono<CartOutDto> changeItemsInCart(long itemId, Action action);
}
