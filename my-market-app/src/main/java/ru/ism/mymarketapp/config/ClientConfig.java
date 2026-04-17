package ru.ism.mymarketapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ism.mymarketapp.client.ApiClient;
import ru.ism.mymarketapp.client.api.PayControllerApi;

@Configuration
public class ClientConfig {

    @Value("${client.url}")
    private String url;

    @Bean
    public PayControllerApi payControllerApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        return new PayControllerApi(apiClient);
    }
}
