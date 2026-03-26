package ru.ism.mymarketapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ism.mymarketapp.module.Item;
import ru.ism.mymarketapp.module.ItemWithQuantity;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.module.dto.out.ItemOutDto;
import ru.ism.mymarketapp.module.dto.out.ItemShortOutDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "itemWithQuantity.item_id")
    @Mapping(target = "count", source = "itemWithQuantity.quantity")
    @Mapping(target = "imgPath", expression = "java(\"image/\" + item.getItem_id())")
    ItemOutDto toItemMapperDto(ItemWithQuantity itemWithQuantity, Item item);

    @Mapping(target = "item_id", ignore = true)
    Item toItem(ItemInDto itemInDto);

    @Mapping(target = "id", source = "item.item_id")
    ItemShortOutDto toItemShortOutDto(Item item, int count);
}
