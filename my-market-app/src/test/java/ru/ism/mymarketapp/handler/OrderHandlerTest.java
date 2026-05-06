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
    void getOrders() {
        when(orderService.getOrders()).thenReturn(Flux.just(new OrderOutDto(1, List.of(), 2)));
        client.get().uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void getOrderById() {
        when(orderService.getOrder(anyLong())).thenReturn(Mono.just(new OrderOutDto(1, List.of(), 2)));
        client.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getOrders_anonymous() {
        when(orderService.getOrders()).thenReturn(Flux.just(new OrderOutDto(1, List.of(), 2)));
        client.get().uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection();
        verify(orderService, never()).getOrders();
    }
}