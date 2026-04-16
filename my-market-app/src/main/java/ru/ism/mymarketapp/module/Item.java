package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Table(name = "items", schema = "my_shop")
@NoArgsConstructor
@Data
public class Item implements Serializable {
    @Id
    private long id;
    private String title;
    private String description;
    private long price;
}
