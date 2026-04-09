package ru.ism.mymarketapp.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.config.ItemsRouter;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.CartService;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = {CartHandler.class, ItemsRouter.class})
class CartHandlerTest {

    @Autowired
    private WebTestClient client;
    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private CartItemService cartItemService;
    @MockitoBean
    private ItemHandler itemHandler;
    @MockitoBean
    private OrderHandler orderHandler;

    @Test
    void getCart() {
        when(cartService.getItemInCart()).thenReturn(Mono.just(new CartOutDto(List.of(), 2)));
        client.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void changeItemInCart() {
        when(cartService.getItemInCart()).thenReturn(Mono.just(new CartOutDto(List.of(), 2)));
when(cartItemService.changeItemInCart(anyLong(), any())).thenReturn(Mono.empty());
client.get().uri("/cart/items?id=1&action=PLUS")
        .exchange()
        .expectStatus().isOk();
    }
}