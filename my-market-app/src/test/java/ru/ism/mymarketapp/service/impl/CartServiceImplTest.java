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
import ru.ism.mymarketapp.service.CartService;
import ru.ism.mymarketapp.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class CartServiceImplTest {

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
    @Autowired
    private CartService cartService;

    @Test
    @SneakyThrows
    void getItemInCart() {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setTitle("title");
        itemInDto.setDescription("description");
        itemInDto.setPrice(10L);
        long itemId = itemService.createItem(itemInDto).block();
        cartItemService.changeItemInCart(itemId, Action.PLUS).block();
        var cart = cartService.getItemInCart().block();
        assertNotNull(cart);
        assertEquals(1, cart.items().size());
        assertEquals(itemId, cart.items().get(0).id());
    }
}