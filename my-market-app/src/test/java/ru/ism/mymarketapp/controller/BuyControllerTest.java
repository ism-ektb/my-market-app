package ru.ism.mymarketapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.service.OrderService;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = BuyController.class)
class BuyControllerTest {

    @Autowired
    private WebTestClient webClient;
    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder() {
        when(orderService.buy()).thenReturn(Mono.just(1L));
        webClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

    }
}