package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.dto.in.RegRequest;
import ru.ism.mymarketapp.repository.UserRepository;
import ru.ism.mymarketapp.service.UserService;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }


}
