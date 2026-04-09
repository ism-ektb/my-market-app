package ru.ism.mymarketapp.service;

import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;

public interface CartService {
    /**
     * Получить список товаров в корзине
     * @return
     */
    Mono<CartOutDto> getItemInCart();
}
