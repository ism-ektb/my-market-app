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
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.ItemService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class ItemServiceImplTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ItemWithQuantityRepo itemWithQuantityRepo;
    @Autowired
    private CartItemWithQuantityRepo cartItemWithQuantityRepo;
    @Autowired
    private ItemService itemService;

    @Test
    @SneakyThrows
    void getItem() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        var item = itemService.getItem(itemId).block();
        assertNotNull(item);
        assertEquals(itemId, item.id());
        assertEquals(1, item.count());
    }

    @Test
    @SneakyThrows
    void getItems() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title2");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        ItemInDto itemInDto1 = new ItemInDto();
        itemInDto1.setTitle("title1");
        itemInDto1.setDescription("description");
        itemInDto1.setPrice(10L);
        long itemId2 = itemService.createItem(itemInDto1).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        var items = itemService.searchItems(Map.of()).block();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(3, items.get(0).size());
    }
}