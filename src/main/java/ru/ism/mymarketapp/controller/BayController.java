package ru.ism.mymarketapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.service.OrderService;

@Controller
@RequiredArgsConstructor
public class BayController {

    private final OrderService orderService;

    @PostMapping("/buy")
    public Mono<String> createOrder() {
        return orderService.buy()
                .map(order_id -> String.format("redirect:/orders/%d?newOrder=true", order_id));
    }
}
