package ru.ism.market.module.dto.in;

import lombok.Data;

@Data
public class ItemInDto {
    private String title;
    private String description;
    private long price;
}
