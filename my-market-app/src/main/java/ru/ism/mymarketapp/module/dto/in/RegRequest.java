package ru.ism.mymarketapp.module.dto.in;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegRequest {
    private String email;
    private String password;
}
