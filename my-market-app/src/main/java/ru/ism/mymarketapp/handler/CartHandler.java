package ru.ism.mymarketapp.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.CartService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartHandler {

    private final CartService cartService;
    private final CartItemService cartItemService;

    /**
     * Подписаться на корзину с заказами
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> getCart(ServerRequest request) {
        boolean bayError = request.queryParam("bayError")
                .map(Boolean::parseBoolean)
                .orElse(false);
        return ServerResponse.ok()
                .render("cart", Map.of("cart", cartService.getItemInCartFull(),
                        "bayError", bayError, "username", getUsername()));
    }

    /**
     * Подписаться на изменение количества товара с itemId в корзине
     *
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
                .render("cart", Map.of("cart", cartItemService.changeItemInCart(itemid, action)
                        .then(cartService.getItemInCartFull()),
                "username", getUsername()));
    }

    private Mono<String> getUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }


}
