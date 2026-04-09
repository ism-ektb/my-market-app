package ru.ism.mymarketapp.module.dto.in;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ItemInDto {
    private String title;
    private String description;
    private long price;
}
