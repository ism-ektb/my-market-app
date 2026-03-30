package ru.ism.mymarketapp.service;

import ru.ism.mymarketapp.module.enums.Action;
import reactor.core.publisher.Mono;

public interface CartItemService {
    /**
     * Изменение количества товара с номером itemId в корзине
     * @param itemId
     * @param action
     * @return
     */
    Mono<Void> changeItemInCart(long itemId, Action action);
}
