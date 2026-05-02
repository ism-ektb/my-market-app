package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthenticationRequestToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.User;
import ru.ism.mymarketapp.module.dto.in.RegRequest;
import ru.ism.mymarketapp.repository.UserRepository;
import ru.ism.mymarketapp.service.RegService;

@Service
@RequiredArgsConstructor
public class RegServiceImpl implements RegService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private final ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<User> save(RegRequest regRequest) {
        User user = new User();
        user.setEmail(regRequest.getEmail());
        user.setPassword("{bcrypt}" + passwordEncoder.encode(regRequest.getPassword()));
        return userRepository.save(user);
    }
}
