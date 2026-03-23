package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.CartRepository;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartService;

@Component
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final ItemWithQuantityRepo itemWithQuantityRepo;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final ItemMapper itemMapper;

    /**
     * Получить список товаров в корзине
     *
     * @return
     */
    @Override
    public Mono<CartOutDto> getItemInCart() {

        return itemWithQuantityRepo.findAllById(cartItemWithQuantityRepo
                        .findAll()
                        .map(CartItemWithQuantity::getItem_with_quantity_id))
                .publishOn(Schedulers.boundedElastic())
                .map(iwq -> itemMapper.toItemMapperDto(iwq, itemRepository.findById(iwq.getItem_id()).block()))
                .collectList()
                .map(list -> {
                    long sum = list.stream()
                            .map(item -> item.count() * item.price())
                            .mapToLong(Long::longValue).sum();
                    return new CartOutDto(list, sum);
                });
    }

    /**
     * Изменить число товаров с номером itemId в корзине
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public Mono<CartOutDto> changeItemsInCart(long itemId, Action action) {

        return itemWithQuantityRepo.findAllById(cartItemWithQuantityRepo
                        .findAll()
                        .map(CartItemWithQuantity::getItem_with_quantity_id))
                .publishOn(Schedulers.boundedElastic())
                .map(iwq -> {
                            if (iwq.getItem_id() == itemId) {
                                switch (action) {
                                    case PLUS -> {
                                        iwq.setQuantity(iwq.getQuantity() + 1);
                                        itemWithQuantityRepo.save(iwq).block();
                                    }
                                    case MINUS -> {
                                        if (iwq.getQuantity() > 1) {
                                            iwq.setQuantity(iwq.getQuantity() - 1);
                                            itemWithQuantityRepo.save(iwq).block();
                                        } else {
                                            iwq.setQuantity(0);
                                            itemWithQuantityRepo.deleteById(iwq.getItem_id()).block();
                                        }
                                    }
                                    case DELETE -> {
                                        iwq.setQuantity(0);
                                        itemWithQuantityRepo.deleteById(iwq.getItem_id()).block();
                                    }
                                }
                            }
                            return iwq;
                        }
                ).filter(iwq -> iwq.getQuantity() > 0)
                .publishOn(Schedulers.boundedElastic())
                .map(iwq -> itemMapper.toItemMapperDto(iwq, itemRepository.findById(iwq.getItem_id()).block()))
                .collectList()
                .map(list -> {
                    long sum = list.stream()
                            .map(item -> item.count() * item.price())
                            .mapToLong(Long::longValue).sum();
                    return new CartOutDto(list, sum);
                });
    }
}
