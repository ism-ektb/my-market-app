package ru.ism.market.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ism.market.mapper.ItemMapper;
import ru.ism.market.module.Cart;
import ru.ism.market.module.ItemWithQuantity;
import ru.ism.market.module.Order;
import ru.ism.market.module.dto.out.OrderOutDto;
import ru.ism.market.repository.CartRepository;
import ru.ism.market.repository.OrderRepository;
import ru.ism.market.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ItemMapper itemMapper;

    /**
     * Получение списка заказов
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderOutDto> getOrders() {
        return itemMapper.toOrderOutDtoList(orderRepository.findAll());
    }

    /**
     * Получение заказа по id
     *
     * @param orderId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public OrderOutDto getOrder(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return itemMapper.toOrderOutDto(order);
    }

    /**
     * Создание нового заказа из содержимого корзины
     *
     * @return
     */
    @Override
    public OrderOutDto buy() {
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        List<ItemWithQuantity> listInCart = cart.getItemsWithQuantity();
        long totalSum = listInCart.stream()
                .mapToLong(iwq -> iwq.getItem().getPrice() * iwq.getQuantity()).sum();
        Order order = orderRepository.save(Order.builder().itemsWithQuantity(listInCart).totalSum(totalSum).build());
        cart.setItemsWithQuantity(new ArrayList<>());
        return itemMapper.toOrderOutDto(order);

    }
}
