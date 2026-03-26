package ru.ism.mymarketapp.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.ItemService;
import ru.ism.mymarketapp.service.OrderService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers

class OrderServiceImplIntegralTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;



    @Test
    @SneakyThrows
    void buy() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        itemService.addItemInCart(String.format("%d", itemId), Action.PLUS).block();
        itemService.addItemInCart(String.format("%d", itemId), Action.PLUS).block();
        itemService.addItemInCart(String.format("%d", itemId), Action.MINUS).block();
        itemService.addItemInCart(String.format("%d", itemId), Action.PLUS).block();
        long orderId = orderService.buy().block();
        OrderOutDto orderOutDto = orderService.getOrder(orderId).block();
        assertNotNull(orderOutDto);
        assertEquals(20L, orderOutDto.totalSum());
        assertEquals(itemId, orderOutDto.items().get(0).id());
    }
}