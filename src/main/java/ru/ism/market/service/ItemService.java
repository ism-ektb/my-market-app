package ru.ism.market.service;

import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemsOutDto;
import ru.ism.market.module.enums.Action;

public interface ItemService {

    /**
     * Получить товар по Id
     * @param id
     * @return
     */
    ItemOutDto getItem(long id);

    /**
     * Изменить количество товара в корзине на единицу
     * @param itemId
     * @param action
     * @return
     */
    ItemOutDto addItemInCart(long itemId, Action action);

    /**
     * Поиск товаров по ключевому слову
     * @param keyword
     * @param pageNumber
     * @param pageSize
     * @return
     */
    ItemsOutDto searchItems(String keyword, int pageNumber, int pageSize);
}
