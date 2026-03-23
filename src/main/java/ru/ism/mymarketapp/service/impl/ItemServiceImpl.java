package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.Item;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.dto.out.Paging;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.service.ItemService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    /**
     * Получить товар из БД по ID
     *
     * @param id
     * @return
     */
    @Override
    public Mono<ItemOutDto> getItem(String id) {
        return itemRepository.findById(Long.valueOf(id)).map(itemMapper::toItemOutDto);
    }

    /**
     * Изменить количество товара в корзине на единицу
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public Mono<ItemOutDto> addItemInCart(String itemId, Action action) {
        var dto = new ItemOutDto(1L, "title", "описание", "image/1.jpg", 1L, 10);
        return Mono.just(dto);
    }

    /**
     * Поиск товаров по ключевому слову с пагинацией. Если слово пустое выводятся все значения.
     * Для отображения в Thymeleaf шаблоне список товаров делится на подсписки из трех позиций.
     * Если в последнем элементе этого списка меньше трех позиций в него добавляются "заглушки" c id = -1
     *
     * @return
     */
    @Override
    public Mono<List<List<ItemOutDto>>> searchItems(Map<String, String> query) {

        var dtos = List.of(List.of(
                new ItemOutDto(1L, "title1", "desc1", "image/1.jpg", 1L, 10),
                new ItemOutDto(1L, "title1", "desc1", "image/1.jpg", 1L, 10),
                new ItemOutDto(-1L, "title1", "desc1", "image/1.jpg", 1L, 0)
        ));
        return Mono.just(dtos);
    }

    /**
     * Сохранение информации о позиции
     *
     * @param itemInDto
     */
    @Override
    public Mono<Long> createItem(ItemInDto itemInDto) throws IOException {
        return itemRepository.save(itemMapper.toItem(itemInDto)).map(Item::getItem_id);
    }

    /**
     * Получение изображения из БД в виде списка байт
     *
     * @param id
     * @return
     */
    @Override
    public byte[] getImage(long id) {
        return new byte[0];
    }

    /**
     * Запрос пагинации
     *
     * @param query
     * @return
     */
    @Override
    public Mono<Paging> getPage(Map<String, String> query) {
        var paging = new Paging(3, 0, false, true);
        return Mono.just(paging);
    }
}
