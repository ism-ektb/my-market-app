package ru.ism.mymarketapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.service.OrderService;

/**
Контроллер покупки товаров
 */
@Controller
@RequiredArgsConstructor
public class BuyController {

    private final OrderService orderService;

    @PostMapping("/buy")
    public Mono<String> createOrder() {
        return orderService.buy()
                .map(order_id -> String.format("redirect:/orders/%d?newOrder=true", order_id))
                .onErrorReturn(WebClientResponseException.BadRequest.class, "redirect:/cart/items?bayError=true");
    }
}
