package ru.ism.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ism.market.module.Item;
import ru.ism.market.module.ItemWithQuantity;
import ru.ism.market.module.Order;
import ru.ism.market.module.dto.in.ItemInDto;
import ru.ism.market.module.dto.out.ItemOutDto;
import ru.ism.market.module.dto.out.ItemShortOutDto;
import ru.ism.market.module.dto.out.OrderOutDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "item.item_id")
    @Mapping(target = "imgPath", expression = "java(\"image/\" + item.getItem_id())")
    ItemOutDto toItemOutDto(Item item, int count);

    @Mapping(target = "item_id", ignore = true)
    Item toItem(ItemInDto itemInDto);

    @Mapping(target = "id", source = "iwq.item.item_id")
    @Mapping(target = "title", source = "iwq.item.title")
    @Mapping(target = "description", source = "iwq.item.description")
    @Mapping(target = "price", source = "iwq.item.price")
    @Mapping(target = "imgPath", expression = "java(\"image/\" + iwq.getItem().getItem_id())")
    @Mapping(target = "count", source = "iwq.quantity")
    ItemOutDto toItemOutDto(ItemWithQuantity iwq);

    List<ItemOutDto> toItemOutDtoList(List<ItemWithQuantity> iwqList);

    @Mapping(target = "id", source = "iwq.item.item_id")
    @Mapping(target = "title", source = "iwq.item.title")
    @Mapping(target = "price", source = "iwq.item.price")
    @Mapping(target = "count", source = "iwq.quantity")
    ItemShortOutDto toItemShortOutDto(ItemWithQuantity iwq);

    List<ItemShortOutDto> toItemShortOutDtoList(List<ItemWithQuantity> iwqList);

    @Mapping(target = "items", source = "itemsWithQuantity")
    OrderOutDto toOrderOutDto(Order order);

    List<OrderOutDto> toOrderOutDtoList(List<Order> orderList);
}
