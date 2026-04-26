package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "carts", schema = "my_shop")
@NoArgsConstructor
public class Cart {
    @Id
    private long cart_id;
    private String name;

}
