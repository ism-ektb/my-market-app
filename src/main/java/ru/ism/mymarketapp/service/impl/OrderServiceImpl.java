package ru.ism.mymarketapp.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.out.ItemShortOutDto;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.service.OrderService;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    /**
     * Получение списка заказов
     *
     * @return
     */
    @Override
    public Flux<OrderOutDto> getOrders() {
        return Flux.just(new OrderOutDto(1L, List.of(new ItemShortOutDto(1L, "title", 2L, 3)), 40L));
    }

    /**
     * Получение заказа по id
     *
     * @param orderId
     * @return
     */
    @Override
    public Mono<OrderOutDto> getOrder(long orderId) {
        return Mono.just(new OrderOutDto(1L, List.of(new ItemShortOutDto(1L, "title", 2L, 3)), 40L));
    }

    /**
     * Создание нового заказа из содержимого корзины
     *
     * @return
     */
    @Override
    public Mono<Void> buy() {
        return Mono.empty();
    }
}
