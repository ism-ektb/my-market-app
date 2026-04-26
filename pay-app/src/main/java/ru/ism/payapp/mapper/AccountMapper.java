package ru.ism.payapp.mapper;

import org.mapstruct.Mapper;
import ru.ism.payapp.domain.Account;
import ru.ism.payapp.domain.BalanceDto;
@Mapper(componentModel = "spring")
public interface AccountMapper {
    BalanceDto toBalanceDto(Account account);
}
