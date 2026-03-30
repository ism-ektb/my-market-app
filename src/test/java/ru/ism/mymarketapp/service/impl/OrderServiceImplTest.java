package ru.ism.mymarketapp.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.Item;
import ru.ism.mymarketapp.module.ItemWithQuantity;
import ru.ism.mymarketapp.module.Order;
import ru.ism.mymarketapp.module.OrderItemWithQuantity;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.OrderItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.OrderRepository;
import ru.ism.mymarketapp.service.OrderService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class OrderServiceImplTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderItemWithQuantityRepo orderItemWithQuantityRepo;
    @MockitoBean
    private ItemWithQuantityRepo itemWithQuantityRepo;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    void getOrders() {
        Order order = new Order();
        order.setOrder_id(1L);
        when(orderRepository.findAll()).thenReturn(Flux.just(order));
        Item item = new Item();
        item.setId(1L);
        item.setPrice(10L);
        when(itemRepository.findById(anyLong())).thenReturn(Mono.just(item));
        ItemWithQuantity iwq = new ItemWithQuantity();
        iwq.setItem_id(1L);
        iwq.setQuantity(2);
        iwq.setItem_id(1L);
        when(itemWithQuantityRepo.findById(anyLong())).thenReturn(Mono.just(iwq));
        OrderItemWithQuantity oiwq = new OrderItemWithQuantity();
        oiwq.setOrderId(1L);
        oiwq.setItem_with_quantity_id(1L);
        when(orderItemWithQuantityRepo.findAllByOrderId(anyLong())).thenReturn(Flux.just(oiwq));

        List<OrderOutDto> list = orderService.getOrders().collectList().block();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(10L, list.get(0).items().get(0).price());
    }
}