package ru.ism.mymarketapp.service;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.dto.in.RegRequest;

public interface RegService {
    Mono<User> save (RegRequest regRequest);
}
