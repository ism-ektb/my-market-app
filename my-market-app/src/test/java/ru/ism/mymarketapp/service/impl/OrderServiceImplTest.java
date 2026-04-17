package ru.ism.mymarketapp.service.impl;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class OrderServiceImplTest {

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.4.2-bookworm"));

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemWithQuantityRepo itemWithQuantityRepo;
    @Autowired
    private OrderItemWithQuantityRepo orderItemWithQuantityRepo;
    @Autowired
    private OrderRepository orderRepository;


    @Test
    void getOrders() {
        Item item = new Item();
        item.setTitle("title1");
        item.setDescription("description");
        item.setPrice(3L);
        item = itemRepository.save(item).block();
        var iwq = itemWithQuantityRepo.save(new ItemWithQuantity(item.getId(), 1)).block();
        Order order = orderRepository.save(new Order()).block();
        var oiwq = new OrderItemWithQuantity();
        oiwq.setItemId(item.getId());
        oiwq.setOrderId(order.getOrder_id());
        oiwq.setItem_with_quantity_id(iwq.getId());
        orderItemWithQuantityRepo.save(oiwq).block();
        var list = orderService.getOrders().collectList().block();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(3L, list.get(0).items().get(0).price());
    }

    @Test
    void getOrder() {
        Item item = new Item();
        item.setTitle("title");
        item.setDescription("description");
        item.setPrice(3L);
        item = itemRepository.save(item).block();
        var iwq = itemWithQuantityRepo.save(new ItemWithQuantity(item.getId(), 1)).block();
        Order order = orderRepository.save(new Order()).block();
        var oiwq = new OrderItemWithQuantity();
        oiwq.setItemId(item.getId());
        oiwq.setOrderId(order.getOrder_id());
        oiwq.setItem_with_quantity_id(iwq.getId());
        orderItemWithQuantityRepo.save(oiwq).block();
        OrderOutDto order1 = orderService.getOrder(order.getOrder_id()).block();
        assertNotNull(order1);
        assertEquals(3L, order1.items().get(0).price());
    }
}