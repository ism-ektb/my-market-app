package ru.ism.mymarketapp.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.service.OrderService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderService orderService;

    /**
     * Подписаться на список заказов
     * @param request
     * @return
     */
    public Mono<ServerResponse> getOrders(ServerRequest request) {
        return ServerResponse.ok()
                .render("orders", Map.of("orders", orderService.getOrders(), "username", getUsername()));
    }

    /**
     * Подписаться на заказ с номером Id
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        long orderId = Long.parseLong(request.pathVariable("id"));
        boolean newOrder = request.queryParam("newOrder")
                .map(Boolean::parseBoolean)
                .orElse(false);
        return ServerResponse.ok().render("order",
                Map.of("order", orderService.getOrder(orderId), "newOrder", newOrder,
                        "username", getUsername()));
    }

    private Mono<String> getUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }

}
