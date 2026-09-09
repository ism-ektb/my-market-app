package ru.ism.mymarketapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.in.RegRequest;
import ru.ism.mymarketapp.service.RegService;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * Контроллер регистрации нового пользователя.
 * Сразу после успешной регистрации проходит аутентификация.
 */
@Controller
@RequiredArgsConstructor
public class RegController {

    @Autowired
    private RegService regService;


    private final ReactiveAuthenticationManager authenticationManager;

    @GetMapping("/registration")
    public Mono<String> showForm(Model model) {
        model.addAttribute("user", new RegRequest());

        return Mono.empty().thenReturn("registr");
    }

    @PostMapping("/registration")
    public Mono<String> processForm(@ModelAttribute RegRequest user, ServerWebExchange exchange) {

        return regService.save(user)
                .flatMap(user1 -> {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user1, user.getPassword(), user1.getAuthorities());
                    return authenticationManager.authenticate(authToken);
                })
                .flatMap(authentication -> ReactiveSecurityContextHolder.getContext()
                        .doOnSuccess(securityContext ->
                                securityContext.setAuthentication(authentication)))
                .flatMap(securityContext -> exchange.getSession()
                        .doOnSuccess(session -> session.getAttributes()
                                .put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext)))
                .thenReturn("redirect:/");

    }
}

