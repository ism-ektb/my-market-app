package ru.ism.mymarketapp.service.impl;

import com.redis.testcontainers.RedisContainer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class CartItemServiceImplTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.4.2-bookworm"));

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
    void changeItemInCart_addInCart() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        var ciwq = cartItemWithQuantityRepo.findByItemId(itemId).block();
        assertNotNull(ciwq);
        var iwq = itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id()).block();
        assertNotNull(iwq);
        assertEquals(1, iwq.getQuantity());
        cartItemService.changeItemInCart(itemId, Action.MINUS).block();
    }

    @Test
    @SneakyThrows
    void changeItemInCart_deleteInCart() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title1");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        cartItemService.changeItemInCart(itemId, Action.MINUS).block();
        var ciwqs = cartItemWithQuantityRepo.findAll().collectList().block();
        assertNotNull(ciwqs);
        assertEquals(0, ciwqs.size());
    }
}