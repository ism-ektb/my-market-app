package ru.ism.mymarketapp.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.config.ItemsRouter;
import ru.ism.mymarketapp.module.dto.out.OrderOutDto;
import ru.ism.mymarketapp.service.OrderService;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = {OrderHandler.class, ItemsRouter.class})
class OrderHandlerTest {

    @Autowired
    private WebTestClient client;
    @MockitoBean
    private ItemHandler itemHandler;
    @MockitoBean
    private CartHandler cartHandler;
    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrders() {
        when(orderService.getOrders()).thenReturn(Flux.just(new OrderOutDto(1, List.of(), 2)));
        client.get().uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getOrderById() {
        when(orderService.getOrder(anyLong())).thenReturn(Mono.just(new OrderOutDto(1, List.of(), 2)));
        client.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk();
    }
}