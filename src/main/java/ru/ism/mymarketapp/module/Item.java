package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "items", schema = "my_shop")
@NoArgsConstructor
@Data
public class Item {
    @Id
    private long item_id;
    private String title;
    private String description;
    private long price;
}
