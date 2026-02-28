package ru.ism.market.service;

import ru.ism.market.module.dto.out.OrderOutDto;

import java.util.List;

public interface OrderService {

    /**
     * Получение списка заказов
     * @return
     */
    List<OrderOutDto> getOrders();

    /**
     * Получение заказа по id
     * @param orderId
     * @return
     */
    OrderOutDto getOrder(long orderId);

    /**
     * Создание нового заказа из содержимого корзины
     * @return
     */
    OrderOutDto buy();
}
