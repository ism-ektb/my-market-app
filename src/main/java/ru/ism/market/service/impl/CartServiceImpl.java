package ru.ism.market.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ism.market.mapper.ItemMapper;
import ru.ism.market.module.Cart;
import ru.ism.market.module.Item;
import ru.ism.market.module.ItemWithQuantity;
import ru.ism.market.module.dto.out.CartOutDto;
import ru.ism.market.module.enums.Action;
import ru.ism.market.repository.CartRepository;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.repository.ItemWithQuantityRepo;
import ru.ism.market.service.CartService;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final ItemWithQuantityRepo itemWithQuantityRepo;

    /**
     * Получить список товаров в корзине
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public CartOutDto getCart() {
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        long sum = cart.getItemsWithQuantity().stream()
                .map(iwq -> iwq.getItem().getPrice() * iwq.getQuantity())
                .reduce(0L, Long::sum);
        return new CartOutDto(itemMapper.toItemOutDtoList(cart.getItemsWithQuantity()), sum);
    }

    /**
     * Изменить число товаров с номером itemId в корзине
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public CartOutDto changeItemsInCart(long itemId, Action action) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        ItemWithQuantity itemWithQuantity = cart.getItemsWithQuantity().stream()
                .filter(iwq -> iwq.getItem().getItem_id() == itemId).findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));
        int quantity = itemWithQuantity.getQuantity();
        if (quantity == 1 && action == Action.MINUS) {
            action = Action.DELETE;
        }
        switch (action) {
            case PLUS -> itemWithQuantity.setQuantity(quantity + 1);
            case MINUS -> itemWithQuantity.setQuantity(quantity - 1);
            case DELETE -> {
                cart.getItemsWithQuantity().remove(itemWithQuantity);
                itemWithQuantityRepo.delete(itemWithQuantity);
            }
        }
        long sum = cart.getItemsWithQuantity().stream()
                .map(iwq -> iwq.getItem().getPrice() * iwq.getQuantity())
                .reduce(0L, Long::sum);
        return new CartOutDto(itemMapper.toItemOutDtoList(cart.getItemsWithQuantity()), sum);
    }
}
