package ru.ism.payapp.repositiry;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ism.payapp.domain.Account;

public interface PayRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account>findByUserId(long userId);
}
