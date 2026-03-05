package ru.ism.market.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ism.market.module.Item;
import ru.ism.market.module.enums.Action;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.service.CartService;
import ru.ism.market.service.ItemService;
import ru.ism.market.service.OrderService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class OrderServiceImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;

    @Test
    void buy() {
        Item item = itemRepository.save(Item.builder().title("title").price(1L).build());
        Item item2 = itemRepository.save(Item.builder().title("title2").price(2L).build());
        itemService.addItemInCart(item.getItem_id(), Action.PLUS);
        itemService.addItemInCart(item2.getItem_id(), Action.PLUS);
        itemService.addItemInCart(item2.getItem_id(), Action.PLUS);

        orderService.buy();
        var card = cartService.getCart();
        assertEquals(0, card.items().size());
        var order = orderService.getOrders().get(0);
        assertEquals(2, order.items().size());
        assertEquals(5L, order.totalSum());

    }
}