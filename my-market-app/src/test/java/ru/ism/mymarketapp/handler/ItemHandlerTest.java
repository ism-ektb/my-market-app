package ru.ism.mymarketapp.handler;

import com.redis.testcontainers.RedisContainer;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
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
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureWebTestClient
class ItemHandlerTest {
    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private CartHandler cartHandler;
    @MockitoBean
    private OrderHandler orderHandler;
    @MockitoBean
    private CartItemService cartItemService;
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
    void getItem() {
        ItemOutDto itemOutDto = new ItemOutDto(1L, "", "", "", 1L, 1);
        when(itemService.getItem(anyLong())).thenReturn(Mono.just(itemOutDto));
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
        verify(itemService, times(1)).getItem(anyLong());
    }

    @Test
    @WithMockUser
    void getItem_addInCart() {
        ItemOutDto itemOutDto = new ItemOutDto(1L, "", "", "", 1L, 1);
        when(cartItemService.changeItemInCart(anyLong(), any(Action.class))).thenReturn(Mono.empty());
        when(itemService.getItem(anyLong())).thenReturn(Mono.just(itemOutDto));
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
        verify(cartItemService, times(1)).changeItemInCart(anyLong(), any(Action.class));
    }
}