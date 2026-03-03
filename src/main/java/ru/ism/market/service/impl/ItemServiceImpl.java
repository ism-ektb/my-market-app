package ru.ism.market.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import ru.ism.market.repository.CartRepository;
import ru.ism.market.repository.ImageRepository;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.service.ItemService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final ItemMapper itemMapper;
    private final ImageRepository imageRepository;
    private final Resource resource = new ClassPathResource("no_foto.jpg");

    @Override
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
                new ItemOutDto(2, "name", "description", "image/100", 10L, 1),
                new ItemOutDto(-1, "name", "description", "images/1.jpg", 10L, 1))),
                new Paging(1, 1, true, true));
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

    @Override
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
