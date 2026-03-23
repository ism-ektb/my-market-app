package ru.ism.mymarketapp.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(schema = "my_shop", name = "images")
public class Image {
    @Id
    private long image_id;
    private long number;
    private byte[] image;
}
