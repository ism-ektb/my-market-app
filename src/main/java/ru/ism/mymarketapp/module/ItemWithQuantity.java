package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Единица корзины и заказа. Состоит из сущности товара и его количества в заказе или корзине
 */
@Data
@NoArgsConstructor
@Table(name = "item_with_quantity", schema = "my_shop")
public class ItemWithQuantity {
    @Id
    private long item_with_quantity_id;
    private long item_id;
    private int quantity;
    @Transient
    private Item item;

    public ItemWithQuantity(long item_with_quantity_id, long item_id, int quantity) {
        this.item_with_quantity_id = item_with_quantity_id;
        this.item_id = item_id;
        this.quantity = quantity;
    }
}
