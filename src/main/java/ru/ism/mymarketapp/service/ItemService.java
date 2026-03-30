package ru.ism.mymarketapp.service;

import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.dto.out.Paging;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ItemService {

    /**
     * Получить товар из БД по ID
     * @param id
     * @return
     */
    Mono<ItemOutDto> getItem(long id);

    /**
     * Поиск товаров по ключевому слову с пагинацией. Если слово пустое выводятся все значения.
     * Для отображения в Thymeleaf шаблоне список товаров делится на подсписки из трех позиций.
     * Если в последнем элементе этого списка меньше трех позиций в него добавляются "заглушки" c id = -1
     *
     * @return
     */
    Mono<List<List<ItemOutDto>>> searchItems(Map<String, String> query);

    /**
     * Запрос пагинации
     * @param query
     * @return
     */
    Mono<Paging> getPage(Map<String, String> query);

    /**
     * Сохранение информации о позиции
     *
     * @param itemInDto

     */
    Mono<Long> createItem(ItemInDto itemInDto) throws IOException;
}
