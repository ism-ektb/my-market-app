package ru.ism.mymarketapp.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.config.ItemsRouter;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = {ItemHandler.class, ItemsRouter.class})
class ItemHandlerTest {
    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private CartHandler cartHandler;
    @MockitoBean
    private OrderHandler orderHandler;

    @Test
    void getItem() {
        ItemOutDto itemOutDto = new ItemOutDto(1L, "", "", "", 1L, 1);
        when(itemService.getItem(anyString())).thenReturn(Mono.just(itemOutDto));
        client.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String content = result.getResponseBody();
                    assertNotNull(content);
                    assertTrue(content.contains("<form"));
                });
        verify(itemService, times(1)).getItem(anyString());
    }

    @Test
    void getItem_adsInCart() {
        ItemOutDto itemOutDto = new ItemOutDto(1L, "", "", "", 1L, 1);
        when(itemService.addItemInCart(anyString(), any(Action.class))).thenReturn(Mono.just(itemOutDto));
        client.get()
                .uri("/items/1?action=PLUS")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String content = result.getResponseBody();
                    assertNotNull(content);
                    assertTrue(content.contains("<form"));
                });
        verify(itemService, times(1)).addItemInCart(anyString(), any(Action.class));
    }
}