package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.client.api.PayControllerApi;
import ru.ism.mymarketapp.client.domain.BayDto;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.Order;
import ru.ism.mymarketapp.module.OrderItemWithQuantity;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.dto.out.ItemShortOutDto;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.repository.*;
import ru.ism.mymarketapp.service.CartService;
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
    private final PayControllerApi payControllerApi;
    private final CartService cartService;

    /**
     * Получение списка заказов
     *
     * @return
     */
    @Override
    public Flux<OrderOutDto> getOrders() {
        return getUserId().flatMapMany(orderRepository::findAllByUserId)
                .flatMap(order -> orderItemWithQuantityRepo.findAllByOrderId(order.getOrder_id())
                        .flatMap(oiwq -> itemWithQuantityRepo.findById(oiwq.getItem_with_quantity_id())
                                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                        .map(item -> itemMapper.toItemShortOutDto(item, iwq.getQuantity()))))
                        .collectList()
                        .map(list -> {
                            list.sort(Comparator.comparing(ItemShortOutDto::id));
                            long totalSum = list.stream().mapToLong(i -> (long) i.count() * i.price()).sum();
                            return new OrderOutDto(order.getOrder_id(), list, totalSum);
                        }));
    }

    /**
     * Получение заказа по id
     *
     * @param orderId
     * @return
     */
    @Override
    public Mono<OrderOutDto> getOrder(long orderId) {
        return orderItemWithQuantityRepo.findAllByOrderId(orderId)
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
     * Создание нового заказа из содержимого корзины.
     * Загружаем содержимое корзины из БД. Списываем средства для оплаты.
     * Создаем пустой заказ. Сохраняем номер заказа в переменную.
     * Перебираем содержимое корзины. Создаём новые сущности содержимого заказа на основе содержимого корзины.
     * Удаляем содержимое корзины.
     * Возвращаем номер заказа
     *
     * @return
     */
    @Override
    @Transactional
    public Mono<Long> buy() {
        Order newOrder = new Order();
        return getUserId().flatMap(userId ->
                cartService.getItemInCart()
                .map(cartOutDto -> {
                    BayDto bayDto = new BayDto();
                    bayDto.setUserId(1L);
                    bayDto.setBaySum(cartOutDto.sum());
                    return bayDto;
                })
                .flatMap(payControllerApi::bayRequest)
                .then(orderRepository.save(new Order(userId)))
                .map(order -> {
                    newOrder.setOrder_id(order.getOrder_id());
                    return order;
                })
                .flatMap(order -> cartItemWithQuantityRepo.findAll()
                        .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id())
                                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                        .map(item -> {
                                            var oiwq = new OrderItemWithQuantity();
                                            oiwq.setOrderId(order.getOrder_id());
                                            oiwq.setItem_with_quantity_id(ciwq.getItem_with_quantity_id());
                                            oiwq.setItemId(ciwq.getItemId());
                                            return oiwq;
                                        })))
                        .flatMap(orderItemWithQuantityRepo::save)
                        .then(cartItemWithQuantityRepo.deleteAll())
                        .then()
                        .then(Mono.just(newOrder.getOrder_id()))));
    }

    private Mono<Long> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(object -> (User) object)
                .map(User::getId);
    }
}

