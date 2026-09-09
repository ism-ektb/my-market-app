package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность заказа.
 */
@Table(schema = "my_shop", name = "orders")
@NoArgsConstructor
@Data
public class Order {
    @Id
    private long order_id;
    @Column(value = "user_id")
    private long userId;

    public Order(long userId) {
        this.userId = userId;
    }
}
