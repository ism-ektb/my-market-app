package ru.ism.payapp.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;
import ru.ism.payapp.service.PayService;

@Service
public class PayServiceImpl implements PayService {
    @Override
    public Mono<BalanceDto> getBalance(long userId) {
        return Mono.just(new BalanceDto(1L, 1L));
    }

    @Override
    public Mono<Void> bay(Mono<BayDto> bay) {
        return Mono.empty();
    }
}
