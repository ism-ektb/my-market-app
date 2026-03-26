package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.mapper.ItemMapper;
import ru.ism.mymarketapp.module.CartItemWithQuantity;
import ru.ism.mymarketapp.module.Item;
import ru.ism.mymarketapp.module.ItemWithQuantity;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.dto.out.Paging;
import ru.ism.mymarketapp.module.enums.Action;
import ru.ism.mymarketapp.module.enums.Sorting;
import ru.ism.mymarketapp.repository.CartItemWithQuantityRepo;
import ru.ism.mymarketapp.repository.ItemRepository;
import ru.ism.mymarketapp.repository.ItemWithQuantityRepo;
import ru.ism.mymarketapp.service.ItemService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final ItemWithQuantityRepo itemWithQuantityRepo;

    /**
     * Получить товар из БД по ID проверив есть ли он в корзине
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<ItemOutDto> getItem(String id) {
        return itemRepository.findById(Long.valueOf(id))
                .flatMap(item -> cartItemWithQuantityRepo.findByNumber(item.getItem_id())
                        .flatMap(ciwq -> itemWithQuantityRepo.findById(ciwq.getItem_with_quantity_id())
                                .map(iwq -> itemMapper.toItemMapperDto(iwq, item))));
    }

    /**
     * Изменить количество товара в корзине на единицу
     * Валидируем itemId, проверяем находится ли товар уже в корзине
     * Изменяем число товара в корзине, сохраняем
     * Загружаем товар из базы и преобразуем в ДТО
     *
     * @param itemId
     * @param action
     * @return
     */
    @Override
    public Mono<ItemOutDto> addItemInCart(String itemId, Action action) {
        long item_id = Long.parseLong(itemId);
        return itemRepository.existsById(Long.valueOf(itemId))
                .filter(exist -> exist)
                .switchIfEmpty(Mono.error(new RuntimeException("Bad itemId")))
                .then(itemWithQuantityRepo.findAllById(cartItemWithQuantityRepo
                                .findAll()
                                .map(CartItemWithQuantity::getItem_with_quantity_id))
                        .filter(iwq -> iwq.getItem_id() == item_id).next()
                        .switchIfEmpty(Mono.defer(() -> {
                            var newIwq = new ItemWithQuantity();
                            newIwq.setItem_id(item_id);
                            newIwq.setQuantity(0);
                            return itemWithQuantityRepo.save(newIwq)
                                    .map(ItemWithQuantity::getItem_with_quantity_id)
                                    .flatMap(l -> cartItemWithQuantityRepo.save(new CartItemWithQuantity(l, 1L, newIwq.getItem_id())))
                                    .flatMap(c -> itemWithQuantityRepo.findById(c.getItem_with_quantity_id()));
                        }))
                        .flatMap(iwq -> {
                            if (action == Action.PLUS) {
                                iwq.setQuantity(iwq.getQuantity() + 1);
                                return itemWithQuantityRepo.save(iwq);
                            } else if (action == Action.MINUS && iwq.getQuantity() > 1) {
                                iwq.setQuantity(iwq.getQuantity() - 1);
                                return itemWithQuantityRepo.save(iwq);
                            } else {
                                iwq.setQuantity(0);
                                return itemWithQuantityRepo.deleteById(iwq.getItem_with_quantity_id()).then(Mono.just(iwq));
                            }
                        })
                        .flatMap(iwq -> itemRepository.findById(iwq.getItem_id())
                                .map(item -> itemMapper.toItemMapperDto(iwq, item))));

    }

    /**
     * Поиск товаров по ключевому слову с пагинацией. Если слово пустое выводятся все значения.
     * Для отображения в Thymeleaf шаблоне список товаров делится на подсписки из трех позиций.
     * Если в последнем элементе этого списка меньше трех позиций в него добавляются "заглушки" c id = -1
     *
     * @return
     */

    @Transactional(readOnly = true)
    public Mono<List<List<ItemOutDto>>> searchItems(Map<String, String> query) {
        String search = query.getOrDefault("search", "") + "%";
        Sort sort = switch (Sorting.valueOf(query.getOrDefault("sort", "NO"))) {
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            case NO -> Sort.by("item_id");
        };
        Comparator<ItemOutDto> comparator = switch (Sorting.valueOf(query.getOrDefault("sort", "NO"))) {
            case ALPHA -> Comparator.comparing(ItemOutDto::title);
            case PRICE -> Comparator.comparing(ItemOutDto::price);
            case NO -> Comparator.comparing(ItemOutDto::id);
        };
        int pageNumber = Integer.parseInt(query.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(query.getOrDefault("pageSize", "10"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return itemRepository.findAllByTitleLikeIgnoreCase(search, pageable)
                .flatMap(item -> {
                    return cartItemWithQuantityRepo.findByNumber(item.getItem_id())
                            .map(CartItemWithQuantity::getItem_with_quantity_id)
                            .flatMap(itemWithQuantityRepo::findById)
                            .switchIfEmpty(Mono.just(new ItemWithQuantity(0, item.getItem_id(), 0)))
                            .map(iwq -> itemMapper.toItemMapperDto(iwq, item));
                }).collectList()
                .map(list -> {
                    list.sort(comparator);
                    List<List<ItemOutDto>> list3 = new ArrayList<>(IntStream.range(0, list.size())
                            .boxed()
                            .collect(Collectors.groupingBy(e -> e / 3, Collectors.mapping(list::get, Collectors.toList()))).values());
                    var noItem = new ItemOutDto(-1L, "", "", "", 0, 0);
                    List<ItemOutDto> lastList = list3.get(list3.size() - 1);
                    switch (lastList.size()) {
                        case 2:
                            lastList.add(noItem);
                            break;
                        case 1:
                            lastList.add(noItem);
                            lastList.add(noItem);
                    }
                    return list3;
                });
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
     * Запрос пагинации
     *
     * @param query
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<Paging> getPage(Map<String, String> query) {
        String search = query.getOrDefault("search", "") + "%";
        Sort sort = switch (Sorting.valueOf(query.getOrDefault("sort", "NO"))) {
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            case NO -> Sort.by("item_id");
        };
        int pageNumber = Integer.parseInt(query.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(query.getOrDefault("pageSize", "10"));
        Pageable pageable = PageRequest.of(pageNumber * pageSize + 1, 1, sort);

        return itemRepository.findAllByTitleLikeIgnoreCase(search, pageable)
                .hasElements()
                .map(next -> new Paging(pageSize, pageNumber, pageNumber > 0, next));
    }
}
