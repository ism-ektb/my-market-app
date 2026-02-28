package ru.ism.market.service.impl;

import org.springframework.stereotype.Service;
import ru.ism.market.module.dto.out.ItemShortOutDto;
import ru.ism.market.module.dto.out.OrderOutDto;
import ru.ism.market.service.OrderService;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    /**
     * Получение списка заказов
     *
     * @return
     */
    @Override
    public List<OrderOutDto> getOrders() {
        return List.of(new OrderOutDto( 2L, List.of(new ItemShortOutDto(3L, "name", 30, 1)), 50));

    }

    /**
     * Получение заказа по id
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderOutDto getOrder(long orderId) {
        return new OrderOutDto( 2L, List.of(new ItemShortOutDto(3L, "name", 30, 1)), 50);
    }

    /**
     * Создание нового заказа из содержимого корзины
     *
     * @return
     */
    @Override
    public OrderOutDto buy() {
        return new OrderOutDto( 2L, List.of(new ItemShortOutDto(3L, "name", 30, 1)), 50);

    }
}
