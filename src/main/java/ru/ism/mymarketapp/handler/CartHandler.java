package ru.ism.mymarketapp.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartHandler {

    private final CartService cartService;

    /**
     * Подписаться на корзину с заказами
     * @param request
     * @return
     */
    public Mono<ServerResponse> getCart(ServerRequest request) {
        return ServerResponse.ok()
                .render("cart", Map.of("cart", cartService.getItemInCart()));
    }

    /**
     * Подписаться на изменение количества товара с itemId в корзине
     * @param request
     * @return
     */
    public Mono<ServerResponse> changeItemInCart(ServerRequest request) {
        long itemid = request.queryParam("id")
                .map(Long::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("id is required"));
        Action action = request.queryParam("action")
                .map(Action::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("action is required"));
        return ServerResponse.ok()
                .render("cart", Map.of("cart", cartService.changeItemsInCart(itemid, action)));
    }




}
