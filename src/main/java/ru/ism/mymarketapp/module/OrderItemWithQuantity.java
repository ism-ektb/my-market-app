package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность объединяет номер заказа и сущность включающую товар и его количество
 */
@Data
@Table(schema = "my_shop", name = "order_item_with_quantity")
@NoArgsConstructor
public class OrderItemWithQuantity {
    long id;
    long item_with_quantity_id;
    long number;
    long total;
}
