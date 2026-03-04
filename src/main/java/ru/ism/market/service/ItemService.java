package ru.ism.market.service;

import org.springframework.web.multipart.MultipartFile;
import ru.ism.market.module.dto.in.ItemInDto;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemsOutDto;
import ru.ism.market.module.enums.Action;
import ru.ism.market.module.enums.Sorting;

import java.io.IOException;

public interface ItemService {

    /**
     * Получить товар по Id
     *
     * @param id
     * @return
     */
    ItemOutDto getItem(long id);

    /**
     * Изменить количество товара в корзине на единицу
     *
     * @param itemId
     * @param action
     * @return
     */
    ItemOutDto addItemInCart(long itemId, Action action);

    /**
     * Поиск товаров по ключевому слову с пагинацией. Если слово пустое выводятся все значения.
     * Для отображения в Thymeleaf шаблоне список товаров делится на подсписки из трех позиций.
     * Если в последнем элементе этого списка меньше трех позиций в него добавляются "заглушки" c id = -1
     *
     * @param keyword
     * @param pageNumber
     * @param pageSize
     * @return
     */
    ItemsOutDto searchItems(String keyword, int pageNumber, int pageSize, Sorting sort);

    /**
     * Сохранение информации о позиции
     *
     * @param itemInDto
     * @param file
     */
    void createItem(ItemInDto itemInDto, MultipartFile file) throws IOException;

    /**
     * Получение изображения из БД в виде списка байт
     * @param id
     * @return
     */
    byte[] getImage(long id);
}
