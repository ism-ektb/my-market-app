package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность объединяет номер заказа и сущность включающую товар и его количество
 * Поля orderId и item_with_quantity_id являются двойным первичным ключом.
 * Поля itemId введено для быстрого получения списка товаров в заказе.
 */
@Data
@Table(schema = "my_shop", name = "order_item_with_quantity")
@NoArgsConstructor
public class OrderItemWithQuantity {
    @Column(value = "order_id")
    private long orderId;
    private long item_with_quantity_id;
    @Column(value = "item_id")
    private long itemId;
}
