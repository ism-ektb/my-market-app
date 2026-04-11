package ru.ism.payapp.service;

import reactor.core.publisher.Mono;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;

public interface PayService {
    Mono<BalanceDto> getBalance(long userId);
    Mono<Void> bay(Mono<BayDto> bay);
}
