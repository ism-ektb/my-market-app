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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class CartServiceImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private CartService cartService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @Test
    void changeItemsInCart() {
        Item item = itemRepository.save(Item.builder().title("title").price(1L).build());
        Item item2 = itemRepository.save(Item.builder().title("title2").price(2L).build());
        itemService.addItemInCart(item.getItem_id(), Action.PLUS);
        itemService.addItemInCart(item2.getItem_id(), Action.PLUS);
        var cart = cartService.getCart();
        assertEquals(2, cart.items().size());
        assertEquals(3L, cart.sum());

        cartService.changeItemsInCart(item.getItem_id(), Action.PLUS);
        cart = cartService.getCart();
        assertEquals(2, cart.items().size());
        assertEquals(4L, cart.sum());

        cartService.changeItemsInCart(item2.getItem_id(), Action.MINUS);
        cart = cartService.getCart();
        assertEquals(1, cart.items().size());
        assertEquals(2L, cart.sum());

        cartService.changeItemsInCart(item.getItem_id(), Action.DELETE);
        cart = cartService.getCart();
        assertEquals(0, cart.items().size());
        assertEquals(0L, cart.sum());

    }
}