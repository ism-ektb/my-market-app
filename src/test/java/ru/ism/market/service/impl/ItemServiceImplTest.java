package ru.ism.market.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.ism.market.mapper.ItemMapper;
import ru.ism.market.mapper.ItemMapperImpl;
import ru.ism.market.module.Cart;
import ru.ism.market.module.Item;
import ru.ism.market.module.ItemWithQuantity;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemsOutDto;
import ru.ism.market.module.enums.Sorting;
import ru.ism.market.repository.CartRepository;
import ru.ism.market.repository.ImageRepository;
import ru.ism.market.repository.ItemRepository;
import ru.ism.market.repository.ItemWithQuantityRepo;
import ru.ism.market.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringJUnitConfig(classes = {ItemServiceImpl.class, ItemMapperImpl.class,
        ImageRepository.class, CartRepository.class, ItemRepository.class, ItemWithQuantityRepo.class})
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemMapper itemMapper;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private ImageRepository imageRepository;
    @MockitoBean
    private CartRepository cartRepository;
    @MockitoBean
    private ItemWithQuantityRepo itemWithQuantityRepo;
    Item item = Item.builder().item_id(1L).build();
    ItemWithQuantity itemWithQuantity = new ItemWithQuantity();

    @BeforeEach
    public void setUp() {
        itemWithQuantity.setQuantity(2);
        itemWithQuantity.setItem(item);
    }

    @Test
    void getItemsById() {
        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setItemsWithQuantity(List.of(itemWithQuantity));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().item_id(1).build()));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(newCart));
        ItemOutDto itemOutDto = itemService.getItem(1L);
        assertNotNull(itemOutDto);
        assertEquals(1L, itemOutDto.id());
        assertEquals(2, itemOutDto.count());
    }

    @Test
    void getItemsById_cartEmpty() {
        Cart newCart = new Cart();
        newCart.setId(1L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().item_id(1).build()));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(newCart));
        ItemOutDto itemOutDto = itemService.getItem(1L);
        assertNotNull(itemOutDto);
        assertEquals(1L, itemOutDto.id());
        assertEquals(0, itemOutDto.count());
    }

    @Test
    void searchItems() {
        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setItemsWithQuantity(List.of(itemWithQuantity));
        when(itemRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(Item.builder().item_id(1L).build(), Item.builder().item_id(2L).build())));
        when(cartRepository.findById(anyLong()))
                .thenReturn(Optional.of(newCart));
        ItemsOutDto dto = itemService.searchItems("", 0, 5, Sorting.NO);
        assertNotNull(dto);
        assertEquals(1, dto.items().size());
        assertEquals(-1, dto.items().get(0).get(2).id());
        long id0 = dto.items().get(0).get(0).id();
        if (id0 == 1L) {
            assertEquals(2, dto.items().get(0).get(0).count());
            assertEquals(0, dto.items().get(0).get(1).count());
        } else {
            assertEquals(0, dto.items().get(0).get(0).count());
            assertEquals(2, dto.items().get(0).get(1).count());
        }
    }
}