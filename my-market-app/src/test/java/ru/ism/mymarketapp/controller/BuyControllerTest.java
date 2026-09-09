package ru.ism.mymarketapp.controller;

import com.redis.testcontainers.RedisContainer;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.config.WebClientConfig;
import ru.ism.mymarketapp.service.ImageService;
import ru.ism.mymarketapp.service.ItemService;
import ru.ism.mymarketapp.service.OrderService;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = BuyController.class)
@Testcontainers
class BuyControllerTest {

    @Autowired
    private WebTestClient webClient;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private ItemService imageService;
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
    void createOrder() {
        when(orderService.buy()).thenReturn(Mono.just(1L));
        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

    }

    @Test
    void createOrder_anonymousUser() {
        when(orderService.buy()).thenReturn(Mono.just(1L));
        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
        verify(orderService, never()).buy();
    }

    @Test
    void createOrder_noCSRF() {
        when(orderService.buy()).thenReturn(Mono.just(1L));
        webClient.post().uri("/buy")
                .exchange()
                .expectStatus().is4xxClientError();
        verify(orderService, never()).buy();
    }
}