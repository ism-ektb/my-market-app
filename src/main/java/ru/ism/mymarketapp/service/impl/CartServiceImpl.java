package ru.ism.mymarketapp.service.impl;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartService;

import java.util.List;

@Component
public class CartServiceImpl implements CartService {
    /**
     * Получить список товаров в корзине
     *
     * @return
     */
    @Override
    public Mono<CartOutDto> getItemInCart() {
        var cart = new CartOutDto(List.of(new ItemOutDto(2L, "title", "desc", "1.jpg", 1L, 2)), 3L);
        return Mono.just(cart);
    }

    /**
     * Изменить число товаров с номером itemId в корзине
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public Mono<CartOutDto> changeItemsInCart(long itemId, Action action) {
        var cart = new CartOutDto(List.of(new ItemOutDto(2L, "title", "desc", "1.jpg", 1L, 60)), 3L);
        return Mono.just(cart);
    }
}
