package ru.ism.mymarketapp.handler;

import com.redis.testcontainers.RedisContainer;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.config.ItemsRouter;
import ru.ism.mymarketapp.module.dto.out.CartFullOutDto;
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
    public static KeycloakContainer keycloak;

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.4.2-bookworm"));

    static {
        keycloak = new KeycloakContainer().withRealmImportFile("realm-export.json");
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/my_server");
    }

    @Test
    @WithMockUser
    void getCart() {
        when(cartService.getItemInCartFull()).thenReturn(Mono.just(new CartFullOutDto(List.of(), 2, true, true)));
        client.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void changeItemInCart() {
        when(cartService.getItemInCartFull()).thenReturn(Mono.just(new CartFullOutDto(List.of(), 2, true, true)));
        when(cartItemService.changeItemInCart(anyLong(), any())).thenReturn(Mono.empty());
        client.get().uri("/cart/items?id=1&action=PLUS")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getCart_anonymous() {
        when(cartService.getItemInCartFull()).thenReturn(Mono.just(new CartFullOutDto(List.of(), 2, true, true)));
        client.get().uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection();
        verify(cartService, never()).getItemInCartFull();
    }

    @Test
    void changeItemInCart_anonymous() {
        when(cartService.getItemInCartFull()).thenReturn(Mono.just(new CartFullOutDto(List.of(), 2, true, true)));
        when(cartItemService.changeItemInCart(anyLong(), any())).thenReturn(Mono.empty());
        client.get().uri("/cart/items?id=1&action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection();
        verify(cartService, never()).getItemInCartFull();
        verify(cartItemService, never()).changeItemInCart(anyLong(), any());
    }
}