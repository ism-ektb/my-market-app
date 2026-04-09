package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Единица корзины и заказа. Состоит из сущности товара и его количества в заказе или корзине
 * Сущность используется как для формирования корзины, так и заказа
 */
@Data
@NoArgsConstructor
@Table(name = "item_with_quantity", schema = "my_shop")
public class ItemWithQuantity {
    @Id
    private long id;
    private long item_id;
    private int quantity;

    public ItemWithQuantity(long id, long item_id, int quantity) {
        this.id = id;
        this.item_id = item_id;
        this.quantity = quantity;
    }

    public ItemWithQuantity(long item_id, int quantity) {
        this.item_id = item_id;
        this.quantity = quantity;
    }
}
