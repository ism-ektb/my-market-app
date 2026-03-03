package ru.ism.market.module;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "item_with_quantity")
public class ItemWithQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_with_quantity_seq")
    @SequenceGenerator(name = "items_with_quantity_seq", sequenceName = "items_with_quantity_sequence", allocationSize = 1)
    private long id;
    @OneToOne
    private Item item;
    private int quantity;


}
