package ru.ism.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import ru.ism.mymarketapp.handler.CartHandler;
import ru.ism.mymarketapp.handler.ItemHandler;
import ru.ism.mymarketapp.handler.OrderHandler;

@Configuration
public class ItemsRouter {
    @Bean
    public RouterFunction<ServerResponse> itemRouter(ItemHandler itemHandler, CartHandler cartHandler, OrderHandler orderHandler) {
        return RouterFunctions.route()
                .GET("", itemHandler::getItems)
                .GET("/", itemHandler::getItems)
                .path("/items", itemBuilder -> itemBuilder
                        .GET("/{id}", RequestPredicates.queryParam("action", t -> true), itemHandler::addItemInCart)
                        .GET("/{id}", itemHandler::getItem)
                        .GET("", RequestPredicates.queryParam("action", t -> true), itemHandler::addItemsInCart)
                        .GET("", itemHandler::getItems))
                .path("/cart", cartBuilder -> cartBuilder
                        .GET("/items", RequestPredicates.queryParam("action", t -> true), cartHandler::changeItemInCart)
                        .GET("/items", cartHandler::getCart))
                .path("/buy", bayBuilder -> bayBuilder.POST("", orderHandler::bay))
                .path("/orders", orderBuilders -> orderBuilders.GET("", orderHandler::getOrders)
                        .GET("/{id}", orderHandler::getOrderById))
                .build();
    }
}
