package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
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

    /**
     * Получаем список товаров в корзине, добавляем количество каждой позиции
     * рассчитываем сумму за все товары
     *
     * @return
     */
    @Override
    public Mono<CartOutDto> getItemInCart() {

        return itemWithQuantityRepo.findAllById(cartItemWithQuantityRepo
                        .findAll()
                        .map(CartItemWithQuantity::getItem_with_quantity_id))
                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                        .map(item -> itemMapper.toItemMapperDto(iwq, item)))
                .collectList()
                .map(list -> {
                    long sum = list.stream()
                            .map(item -> item.count() * item.price())
                            .mapToLong(Long::longValue).sum();
                    return new CartOutDto(list, sum);
                });
    }
}
