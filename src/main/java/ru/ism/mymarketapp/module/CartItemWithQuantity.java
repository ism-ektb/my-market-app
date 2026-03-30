package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность объединяет корзину и сущность включающую товар и его количество
 * Первичный ключ двойной.
 * Поле itemId введено для быстрого поиска товаров в корзине.
 */
@Data
@NoArgsConstructor
@Table(schema = "my_shop", name = "carts_item_with_quantity")
public class CartItemWithQuantity {
    private long cart_id;
    private long item_with_quantity_id;
    @Column(value = "item_id")
    private long itemId;

    public CartItemWithQuantity(long cart_id, long item_with_quantity_id, long itemId) {
        this.item_with_quantity_id = item_with_quantity_id;
        this.cart_id = cart_id;
        this.itemId = itemId;
    }
}
