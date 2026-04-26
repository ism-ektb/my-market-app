package ru.ism.payapp.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;
import ru.ism.payapp.service.PayService;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PayServiceImplTest {

    @Autowired
    private PayService payService;

    @Container
    @ServiceConnection(type = {R2dbcConnectionDetails.class})
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15");

    @Test
    void getBalance() {
        BalanceDto dto = payService.getBalance(1L).block();
        assertNotNull(dto);
    }

    @Test
    void bay() {
        BayDto dto = new BayDto();
        dto.setUserId(1L);
        dto.setBaySum(10L);
        payService.bay(dto).block();
        dto.setBaySum(1000000L);
        assertThrows(RuntimeException.class, () -> payService.bay(dto).block());
    }
}