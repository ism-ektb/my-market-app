package ru.ism.mymarketapp.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;

public interface OrderService {
    /**
     * Получение списка заказов
     * @return
     */
    Flux<OrderOutDto> getOrders();

    /**
     * Получение заказа по id
     * @param orderId
     * @return
     */
    Mono<OrderOutDto> getOrder(long orderId);

    /**
     * Создание нового заказа из содержимого корзины
     * @return
     */
    Mono<Void> buy();
}
