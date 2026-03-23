package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(schema = "my_shop", name = "carts_item_with_quantity")
public class CartItemWithQuantity {
    private long cart_id;
    private long item_with_quantity_id;
}
