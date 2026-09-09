package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.client.BalanceDto;
import ru.ism.mymarketapp.module.dto.out.CartFullOutDto;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartService;

@Component
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final ItemWithQuantityRepo itemWithQuantityRepo;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final ItemMapper itemMapper;
    private final WebClient webClient;

    @Value("${client.url}")
    private String url;

    /**
     * Получаем список товаров в корзине, добавляем количество каждой позиции
     * рассчитываем сумму за все товары
     *
     * @return
     */
    @Override
    public Mono<CartOutDto> getItemInCart() {
        return getCart();
    }

    /**
     * Получение списка товаров в корзине, общей суммы покупки,
     * идентификатора доступности платежного сервиса и
     * идентификатора достаточности средств для покупки
     *
     * @return
     */
    @Override
    public Mono<CartFullOutDto> getItemInCartFull() {

        return getCart()
                .flatMap(cartOutDto -> getBalance()
                        .map(balanceDto -> balanceDto.getBalance() >= cartOutDto.sum())
                        .map(enoughMoneyToBuy -> new CartFullOutDto(cartOutDto.items(), cartOutDto.sum(), enoughMoneyToBuy, true))
                        .onErrorReturn(new CartFullOutDto(cartOutDto.items(), cartOutDto.sum(), false, false)));
    }

    private Mono<CartOutDto> getCart() {
        return getUserId()
                .flatMap(userId -> itemWithQuantityRepo.findAllById(cartItemWithQuantityRepo
                        .findByCartId(userId)
                        .map(CartItemWithQuantity::getItem_with_quantity_id))
                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                        .map(item -> itemMapper.toItemMapperDto(iwq, item)))
                .collectList()
                .map(list -> {
                    long sum = list.stream()
                            .map(item -> item.count() * item.price())
                            .mapToLong(Long::longValue).sum();
                    return new CartOutDto(list, sum);
                }));
    }

    private Mono<Long> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(object -> (User) object)
                .map(User::getId);
    }

    private Mono<BalanceDto> getBalance() {
        return webClient.get().uri(url + "/amount?userId=1")
                .retrieve()
                .bodyToMono(BalanceDto.class);
    }

}
