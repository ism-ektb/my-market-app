package ru.ism.market.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ism.market.module.Item;
import ru.ism.market.module.dto.in.ItemInDto;
import ru.ism.market.module.dto.out.ItemOutDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", source = "item.item_id")
    @Mapping(target = "imgPath", expression = "java(\"image/\" + item.getItem_id())")
    ItemOutDto toItemOutDto(Item item, int count);

    @Mapping(target = "item_id", ignore = true)
    Item toItem(ItemInDto itemInDto);
}
