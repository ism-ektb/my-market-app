package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.Order;
import ru.ism.mymarketapp.module.OrderItemWithQuantity;
import ru.ism.mymarketapp.module.dto.out.ItemShortOutDto;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.repository.*;
import ru.ism.mymarketapp.service.OrderService;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final OrderItemWithQuantityRepo orderItemWithQuantityRepo;
    private final ItemRepository itemRepository;
    private final ItemWithQuantityRepo itemWithQuantityRepo;
    private final ItemMapper itemMapper;

    /**
     * Получение списка заказов
     *
     * @return
     */
    @Override
    public Flux<OrderOutDto> getOrders() {
        return orderRepository.findAll()
                .flatMap(order -> orderItemWithQuantityRepo.findAllById(order.getOrder_id())
                        .flatMap(oiwq -> itemWithQuantityRepo.findById(oiwq.getItem_with_quantity_id())
                                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                .map(item -> itemMapper.toItemShortOutDto(item, iwq.getQuantity()))))
                        .collectList()
                        .map(list -> {list.sort(Comparator.comparing(ItemShortOutDto::id));
                            return new OrderOutDto(order.getOrder_id(), list, order.getTotal());}));
    }


    /**
     * Получение заказа по id
     *
     * @param orderId
     * @return
     */
    @Override
    public Mono<OrderOutDto> getOrder(long orderId) {
        return orderItemWithQuantityRepo.findAllById(orderId)
                .flatMap(oiwq -> itemWithQuantityRepo.findById(oiwq.getItem_with_quantity_id())
                        .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                .map(item -> itemMapper.toItemShortOutDto(item, iwq.getQuantity()))))
                .collectList()
                .map(list -> {
                    long total = list.stream()
                            .map(itemShortOutDto -> (long) itemShortOutDto.count() * itemShortOutDto.price())
                            .mapToLong(i -> i).sum();
                    return new OrderOutDto(orderId, list, total);
                });
    }

    /**
     * Создание нового заказа из содержимого корзины
     *
     * @return
     */
    @Override
    public Mono<Long> buy() {
        Order newOrder = new Order();
        return orderRepository.save(newOrder)
                .flatMap(order -> cartItemWithQuantityRepo.findAll()
                        .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id())
                                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                        .map(item -> {
                                            var oiwq = new OrderItemWithQuantity();
                                            oiwq.setId(order.getOrder_id());
                                            oiwq.setItem_with_quantity_id(ciwq.getItem_with_quantity_id());
                                            oiwq.setNumber(ciwq.getNumber());
                                            oiwq.setTotal(item.getPrice() * iwq.getQuantity());
                                            return oiwq;
                                        })))
                        .flatMap(orderItemWithQuantityRepo::save)
                        .collectList()
                        .flatMap(list -> {
                            long total = list.stream()
                                    .map(OrderItemWithQuantity::getTotal)
                                    .mapToLong(i -> i).sum();
                            order.setTotal(total);
                            return cartItemWithQuantityRepo.deleteAll()
                                    .then(orderRepository.save(order))
                                    .map(Order::getOrder_id);
                        }));
    }
}

