package ru.ism.mymarketapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.ItemWithQuantity;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.CartItemService;

import java.util.Map;
import java.util.function.Function;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final ItemWithQuantityRepo itemWithQuantityRepo;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final Map<Action, Function<ItemWithQuantity, Mono<Void>>> actionHandlers;

    @Autowired
    public CartItemServiceImpl(ItemWithQuantityRepo itemWithQuantityRepo, CartItemWithQuantityRepo cartItemWithQuantityRepo) {
        this.itemWithQuantityRepo = itemWithQuantityRepo;
        this.cartItemWithQuantityRepo = cartItemWithQuantityRepo;
        this.actionHandlers = Map.of(
                Action.PLUS, this::handlePlus,
                Action.MINUS, this::handleMinus,
                Action.DELETE, this::handleDelete
        );
    }

    /**
     * Изменение количества товара в корзине.
     * Загружаем товар из корзины с номером itemId.
     * Если товара с таким номером нет. То создаем его в корзине с количеством 0.
     * Вызываем функцию, которая изменяет количество товара в корзине и либо сохнаняет измененное значение,
     * либо удаляет из корзины товар с нулевым количеством
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    @Transactional
    public Mono<Void> changeItemInCart(long itemId, Action action) {
        return getUserId()
                .flatMap(userId -> cartItemWithQuantityRepo.findByCartId(userId)
                        .filter(c -> c.getItemId() == itemId)
                        .next()
                        .switchIfEmpty(itemWithQuantityRepo.save(new ItemWithQuantity(itemId, 0))
                                .map(newIwq -> newIwq.getId())
                                .flatMap(iwqId -> cartItemWithQuantityRepo.save(new CartItemWithQuantity(userId, iwqId, itemId))))
                        .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id()))
                        .flatMap(iwq -> actionHandlers.get(action).apply(iwq)));
    }

    /**
     * Увеличение количества товара в корзине. С последующим сохранением
     *
     * @param iwq
     * @return
     */
    private Mono<Void> handlePlus(ItemWithQuantity iwq) {
        iwq.setQuantity(iwq.getQuantity() + 1);
        return itemWithQuantityRepo.save(iwq).then();
    }

    /**
     * Уменьшение товара в корзине с последующим сохранением или каскадным удалением
     *
     * @param iwq
     * @return
     */
    private Mono<Void> handleMinus(ItemWithQuantity iwq) {
        if (iwq.getQuantity() > 1) {
            iwq.setQuantity(iwq.getQuantity() - 1);
            return itemWithQuantityRepo.save(iwq).then();
        } else {
            return itemWithQuantityRepo.deleteById(iwq.getId());
        }
    }

    /**
     * Метод запускает каскадное удаление товара из корзины
     *
     * @param iwq
     * @return
     */
    private Mono<Void> handleDelete(ItemWithQuantity iwq) {
        return itemWithQuantityRepo.deleteById(iwq.getId());
    }

    private Mono<Long> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(object -> (User) object)
                .map(User::getId);
    }
}
