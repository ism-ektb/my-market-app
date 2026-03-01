package ru.ism.market.service;

import org.springframework.web.multipart.MultipartFile;
import ru.ism.market.module.dto.in.ItemInDto;
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

    /**
     * Сохранение информации о позиции
     * @param itemInDto
     * @param file
     */
    void createItem(ItemInDto itemInDto, MultipartFile file);
}
