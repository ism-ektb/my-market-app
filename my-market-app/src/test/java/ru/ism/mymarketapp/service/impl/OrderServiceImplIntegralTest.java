package ru.ism.mymarketapp.service.impl;

import com.redis.testcontainers.RedisContainer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.client.api.PayControllerApi;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.ItemService;
import ru.ism.mymarketapp.service.OrderService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class OrderServiceImplIntegralTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.4.2-bookworm"));

    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartItemService cartItemService;
    @MockitoBean
    private PayControllerApi payControllerApi;

    @Test
    @SneakyThrows
    void buy() {
        when(payControllerApi.bayRequest(any())).thenReturn(Mono.empty());
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        cartItemService.changeItemInCart(itemId, Action.MINUS).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        long orderId = orderService.buy().block();
        OrderOutDto orderOutDto = orderService.getOrder(orderId).block();
        assertNotNull(orderOutDto);
        assertEquals(20L, orderOutDto.totalSum());
        assertEquals(itemId, orderOutDto.items().get(0).id());
    }
}