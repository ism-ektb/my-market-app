package ru.ism.market.service.impl;

import org.springframework.stereotype.Service;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemsOutDto;
import ru.ism.market.module.dto.out.Paging;
import ru.ism.market.module.enums.Action;
import ru.ism.market.service.ItemService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Override
    public ItemOutDto getItem(long id) {
        return new ItemOutDto(1, "name", "описание", "images/1.jpg", 10, 0);
    }

    /**
     * Изменить количество товара в корзине на единицу
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public ItemOutDto addItemInCart(long itemId, Action action) {
        return new ItemOutDto(1, "name", "описание", "images/1.jpg", 10, 1);
    }

    /**
     * Поиск товаров по ключевому слову
     *
     * @param keyword
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public ItemsOutDto searchItems(String keyword, int pageNumber, int pageSize) {
        return new ItemsOutDto(List.of(List.of(new ItemOutDto(2, "name", "description", "images/1.jpg", 10L, 1),
                new ItemOutDto(2, "name", "description", "images/1.jpg", 10L, 1),
                new ItemOutDto(-1, "name", "description", "images/1.jpg", 10L, 1))),
                new Paging(1, 1, true, true));
    }
}
