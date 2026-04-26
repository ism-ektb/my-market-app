package ru.ism.payapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;
import ru.ism.payapp.mapper.AccountMapper;
import ru.ism.payapp.repositiry.PayRepository;
import ru.ism.payapp.service.PayService;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final PayRepository payRepository;
    private final AccountMapper accountMapper;

    @Override
    public Mono<BalanceDto> getBalance(long userId) {
        return payRepository.findByUserId(userId)
                .map(accountMapper::toBalanceDto);
    }

    @Override
    public Mono<Void> bay(BayDto bay) {
        return payRepository.findByUserId(bay.getUserId())
                .flatMap(account -> {if (account.getBalance()>= bay.getBaySum()) {
                account.setBalance(account.getBalance() - bay.getBaySum());
                return payRepository.save(account).then(Mono.empty());}
                    else return Mono.error(new RuntimeException("insufficient funds in the account"));
                } );
    }
}
