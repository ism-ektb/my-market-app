package ru.ism.market.module;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Table(name = "images")
@Getter
@Setter
@SuperBuilder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
    @SequenceGenerator(name = "image_seq", sequenceName = "image_sequence", allocationSize = 1)
    private long image_id;
    @Lob
    private byte[] image_data;
    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item item;

}
