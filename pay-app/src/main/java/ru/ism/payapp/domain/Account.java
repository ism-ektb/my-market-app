package ru.ism.payapp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@Table(name = "accounts", schema = "my_bank")
public class Account {
    @Id
    private long id;
    @Column(value = "user_id")
    private long userId;
    @Column(value = "sum")
    private long balance;
}
