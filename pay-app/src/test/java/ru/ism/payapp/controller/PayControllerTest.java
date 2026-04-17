package ru.ism.payapp.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;
import ru.ism.payapp.service.PayService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = PayController.class)
class PayControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private PayService payService;

    @Test
    void getBalance() {
        when(payService.getBalance(anyLong())).thenReturn(Mono.just(new BalanceDto()));
        webClient.get().uri("/amount?userId=1")
                .exchange().expectStatus().isOk();
        verify(payService, times(1)).getBalance(anyLong());
    }
}