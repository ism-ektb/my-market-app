package ru.ism.mymarketapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<UserDetails> findByEmail(String email);
}
