package ru.ism.mymarketapp.service;

import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.CartFullOutDto;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;

public interface CartService {
    /**
     * Получить список товаров в корзине
     *
     * @return
     */
    Mono<CartOutDto> getItemInCart();

    /**
     * Получение списка товаров в корзине, общей суммы покупки,
     * идентификатора доступности платежного сервиса и
     * идентификатора достаточности средств для покупки     *
     *
     * @return
     */
    Mono<CartFullOutDto> getItemInCartFull();
}
