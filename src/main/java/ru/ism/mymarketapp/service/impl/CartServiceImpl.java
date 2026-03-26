package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.dto.out.CartOutDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.CartRepository;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartService;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
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
    @Transactional(readOnly = true)
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

    /**
     * Изменить число товаров с номером itemId в корзине.
     * Находим товар в корзине число элементов которого надо заменить
     * Изменяем число элементов, вносим изменение в БД.
     * Загружаем корзину из БД. Преобразуем ее в ДТО.
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public Mono<CartOutDto> changeItemsInCart(long itemId, Action action) {

        return cartItemWithQuantityRepo.findByNumber(itemId)
                .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id()))
                .flatMap(iwq ->
                        switch (action) {
                            case PLUS -> {
                                iwq.setQuantity(iwq.getQuantity() + 1);
                                yield itemWithQuantityRepo.save(iwq);
                            }
                            case MINUS -> {
                                if (iwq.getQuantity() > 1) {
                                    iwq.setQuantity(iwq.getQuantity() - 1);
                                    yield itemWithQuantityRepo.save(iwq);
                                } else {
                                    yield itemWithQuantityRepo.deleteById(iwq.getItem_id());
                                }
                            }
                            case DELETE -> {
                                yield itemWithQuantityRepo.deleteById(iwq.getItem_id());
                            }}).then(cartItemWithQuantityRepo.findAll()
                        .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id())
                                .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                        .map(item -> itemMapper.toItemMapperDto(iwq, item))))
                        .collectList()
                        .map(list -> {
                            list.sort(Comparator.comparing(ItemOutDto::id));
                            long sum = list.stream()
                                    .map(item -> item.count() * item.price())
                                    .mapToLong(Long::longValue).sum();
                            return new CartOutDto(list, sum);
                        }));
    }
}
