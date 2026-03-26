package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(schema = "my_shop", name = "order_item_with_quantity")
@NoArgsConstructor
public class OrderItemWithQuantity {
    long id;
    long item_with_quantity_id;
    long number;
    long total;

    public OrderItemWithQuantity(long id, long item_with_quantity_id, long number, long total) {
        this.id = id;
        this.item_with_quantity_id = item_with_quantity_id;
        this.number = number;
        this.total = total;
    }
}
