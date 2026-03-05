package ru.ism.market.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ism.market.module.Item;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.enums.Action;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.service.CartService;
import ru.ism.market.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@RequiredArgsConstructor
class ItemServiceImplIntegralTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartService cartService;

    @Test
    void addItemInCart() {
        Item item = itemRepository.save(Item.builder().title("title").price(1L).build());
        itemService.addItemInCart(item.getItem_id(), Action.PLUS);
        ItemOutDto dto = itemService.getItem(item.getItem_id());
        assertEquals(1, dto.count());
        itemService.addItemInCart(item.getItem_id(), Action.MINUS);
        dto = itemService.getItem(item.getItem_id());
        assertEquals(0, dto.count());
    }

    @Test
    void removeItemFromCart() {
        Item item = itemRepository.save(Item.builder().title("title_test3").price(1L).build());
        itemService.addItemInCart(item.getItem_id(), Action.PLUS);
        var cart = cartService.getCart();
        ItemOutDto dto = cart.items().stream()
                .filter(i -> i.id() == item.getItem_id())
                .findFirst().orElse(null);
        assertNotNull(dto);

        itemService.addItemInCart(item.getItem_id(), Action.MINUS);
        cart = cartService.getCart();
         dto = cart.items().stream()
                .filter(i -> i.id() == item.getItem_id())
                .findFirst().orElse(null);
        assertNull(dto);

    }
}