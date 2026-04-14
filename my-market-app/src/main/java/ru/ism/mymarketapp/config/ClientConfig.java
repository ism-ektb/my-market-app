package ru.ism.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ism.mymarketapp.client.api.PayControllerApi;

@Configuration
public class ClientConfig {

    @Bean
    public PayControllerApi payControllerApi() {
        return new PayControllerApi();
    }
}
