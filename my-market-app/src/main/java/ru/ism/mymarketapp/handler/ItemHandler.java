package ru.ism.mymarketapp.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.service.CartItemService;
import ru.ism.mymarketapp.service.ItemService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemHandler {

    private final ItemService itemService;
    private final CartItemService cartItemService;

    /**
     * Подписаться на информацию о товаре
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> getItem(ServerRequest request) {
        String itemId = request.pathVariable("id");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .render("item", Map.of("item", itemService.getItem(Long.parseLong(itemId)), "username", getUsername()));
    }

    /**
     * Подписаться на изменение количества в корзине товара с номером Id
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> addItemInCart(ServerRequest request) {
        long itemId = Long.parseLong(request.pathVariable("id"));
        var item = request.queryParam("action")
                .map(Action::valueOf)
                .map(action -> cartItemService.changeItemInCart(itemId, action)
                        .then(itemService.getItem(itemId)))
                .orElseThrow(() -> new IllegalArgumentException("Invalid action"));
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .render("item", Map.of("item", item));
    }

    /**
     * Подписаться на поиск списка товаров
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> getItems(ServerRequest request) {
        Map<String, String> query = request.queryParams().asSingleValueMap();
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .render("items", Map.of(
                        "items", itemService.searchItems(query),
                        "search", query.getOrDefault("search", ""),
                        "sort", query.getOrDefault("sort", "NO"),
                        "paging", itemService.getPage(query),
                        "username", getUsername()));
    }

    /**
     * Подписаться на добавление товара из найденного списка в корзину
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> addItemsInCart(ServerRequest request) {
        Map<String, String> query = request.queryParams().toSingleValueMap();
        return ServerResponse.ok().render(String.format("redirect:/items?search=%s&sort=%s&pageNumber=%s&pageSize=%s",
                        query.getOrDefault("search", ""),
                        query.getOrDefault("sort", "NO"),
                        query.getOrDefault("pageNumber", "1"),
                        query.getOrDefault("pageSize", "5")),
                cartItemService.changeItemInCart(Long.parseLong(query.get("id")), Action.valueOf(query.get("action"))));
    }

    private Mono<String> getUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }

}
