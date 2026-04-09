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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CartItemWithQuantityRepo cartItemWithQuantityRepo;
    private final ItemWithQuantityRepo itemWithQuantityRepo;

    /**
     * Получить товар из БД по ID проверив есть ли он в корзине
     *
     * @param
     * @return
     */
    @Override
    public Mono<ItemOutDto> getItem(long id) {
        return itemRepository.findById(id)
                .flatMap(item -> cartItemWithQuantityRepo.findByItemId(id)
                        .map(CartItemWithQuantity::getItem_with_quantity_id)
                        .flatMap(itemWithQuantityRepo::findById)
                        .switchIfEmpty(Mono.just(new ItemWithQuantity(0, id, 0)))
                        .map(iwq -> itemMapper.toItemMapperDto(iwq, item)));
    }

    /**
     * Поиск товаров по ключевому слову с пагинацией. Если слово пустое выводятся все значения.
     * Для отображения в Thymeleaf шаблоне список товаров делится на подсписки из трех позиций.
     * Если в последнем элементе этого списка меньше трех позиций в него добавляются "заглушки" c id = -1
     *
     * @return
     */

    public Mono<List<List<ItemOutDto>>> searchItems(Map<String, String> query) {
        String search = query.getOrDefault("search", "") + "%";
        Sort sort = switch (Sorting.valueOf(query.getOrDefault("sort", "NO"))) {
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            case NO -> Sort.by("id");
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
                    return cartItemWithQuantityRepo.findByItemId(item.getId())
                            .map(CartItemWithQuantity::getItem_with_quantity_id)
                            .flatMap(itemWithQuantityRepo::findById)
                            .switchIfEmpty(Mono.just(new ItemWithQuantity(0, item.getId(), 0)))
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
    @Transactional
    public Mono<Long> createItem(ItemInDto itemInDto) throws IOException {
        return itemRepository.save(itemMapper.toItem(itemInDto)).map(Item::getId);
    }

    /**
     * Запрос пагинации
     *
     * @param query
     * @return
     */
    @Override
    public Mono<Paging> getPage(Map<String, String> query) {
        String search = query.getOrDefault("search", "") + "%";
        Sort sort = switch (Sorting.valueOf(query.getOrDefault("sort", "NO"))) {
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            case NO -> Sort.by("id");
        };
        int pageNumber = Integer.parseInt(query.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(query.getOrDefault("pageSize", "10"));
        Pageable pageable = PageRequest.of((pageNumber + 1) * pageSize, 1, sort);

        return itemRepository.findAllByTitleLikeIgnoreCase(search, pageable)
                .hasElements()
                .map(next -> new Paging(pageSize, pageNumber, pageNumber > 0, next));
    }
}
