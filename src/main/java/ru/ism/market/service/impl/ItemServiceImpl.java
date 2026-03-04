package ru.ism.market.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ism.market.mapper.ItemMapper;
import ru.ism.market.module.Cart;
import ru.ism.market.module.Image;
import ru.ism.market.module.Item;
import ru.ism.market.module.ItemWithQuantity;
import ru.ism.market.module.dto.in.ItemInDto;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemsOutDto;
import ru.ism.market.module.dto.out.Paging;
import ru.ism.market.module.enums.Action;
import ru.ism.market.module.enums.Sorting;
import ru.ism.market.repository.CartRepository;
import ru.ism.market.repository.ImageRepository;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.repository.ItemWithQuantityRepo;
import ru.ism.market.service.ItemService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final ItemMapper itemMapper;
    private final ImageRepository imageRepository;
    private final ItemWithQuantityRepo itemWithQuantityRepo;
    private final Resource resource = new ClassPathResource("no_foto.jpg");

    @Override
    @Transactional(readOnly = true)
    public ItemOutDto getItem(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        int count = cart.getItemsWithQuantity().stream()
                .filter(iwq -> iwq.getItem().getItem_id() == item.getItem_id())
                .findFirst()
                .map(ItemWithQuantity::getQuantity)
                .orElse(0);
        return itemMapper.toItemOutDto(item, count);
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
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        ItemWithQuantity itemWithQuantity = cart.getItemsWithQuantity().stream()
                .filter(iwq -> iwq.getItem().getItem_id() == itemId)
                .findFirst()
                .orElse(null);
        if (itemWithQuantity == null) {
            if (action == Action.MINUS) {
                return itemMapper.toItemOutDto(item,0);
            }
            ItemWithQuantity newItemWithQuantity = itemWithQuantityRepo.save(ItemWithQuantity.builder()
                    .item(item).quantity(1).build());
            cart.getItemsWithQuantity().add(newItemWithQuantity);
            return itemMapper.toItemOutDto(item, 1);
        }
        int quantity = itemWithQuantity.getQuantity();
        if (action == Action.PLUS) {
            itemWithQuantity.setQuantity(quantity + 1);
            return itemMapper.toItemOutDto(item, quantity + 1);
        }
        if (quantity > 1) {
            itemWithQuantity.setQuantity(quantity - 1);
            return itemMapper.toItemOutDto(item, quantity - 1);
        }
        cart.getItemsWithQuantity().remove(itemWithQuantity);
        itemWithQuantityRepo.delete(itemWithQuantity);
        return itemMapper.toItemOutDto(item, 0);
    }

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
    @Override
    @Transactional(readOnly = true)
    public ItemsOutDto searchItems(String keyword, int pageNumber, int pageSize, Sorting sorting) {
        Page<Item> list;
        Sort sort = switch (sorting) {
            case NO -> Sort.unsorted();
            case ALPHA -> Sort.by(Sort.Direction.ASC, "title");
            case PRICE -> Sort.by(Sort.Direction.DESC, "price");
        };
        if (keyword == null || keyword.isEmpty()) {
            list = itemRepository.findAll(PageRequest.of(pageNumber, pageSize, sort));
        } else {
            list = itemRepository.findItemsByTitleLikeIgnoreCase(keyword + "%", PageRequest.of(pageNumber, pageSize, sort));
        }
        if (list.isEmpty()) {
            return new ItemsOutDto(new ArrayList<>(), new Paging(0, 0, false, false));
        }
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("Cart not found"));
        Map<Long, ItemWithQuantity> cartMap = cart.getItemsWithQuantity().stream().collect(Collectors.toMap(a -> a.getItem().getItem_id(), a -> a));
        List<ItemOutDto> items = list.getContent().stream()
                .map(i -> itemMapper.toItemOutDto(i, cartMap.containsKey(i.getItem_id()) ? cartMap.get(i.getItem_id()).getQuantity() : 0)).toList();
        List<List<ItemOutDto>> list3 = new ArrayList<>(IntStream.range(0, list.getContent().size())
                .boxed()
                .collect(Collectors.groupingBy(e -> e / 3, Collectors.mapping(items::get, Collectors.toList()))).values());
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
        return new ItemsOutDto(list3,
                new Paging(pageSize, pageNumber, pageNumber > 0, list.getTotalPages() - 1 > pageNumber));
    }

    /**
     * Сохранение информации о позиции
     *
     * @param itemInDto
     * @param file
     */
    @Override
    public void createItem(ItemInDto itemInDto, MultipartFile file) throws IOException {
        Item item = itemRepository.save(itemMapper.toItem(itemInDto));
        Image image = Image.builder()
                .item(item)
                .image_data(file.getBytes())
                .build();
        imageRepository.save(image);
    }

    /**
     * Получение изображения из БД в виде списка байт
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(long id) {
        try {
            return imageRepository.findById(id)
                    .map(Image::getImage_data)
                    .orElse(resource.getContentAsByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
