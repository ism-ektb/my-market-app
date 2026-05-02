package ru.ism.mymarketapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.*;
import ru.ism.mymarketapp.repository.UserRepository;
import ru.ism.mymarketapp.service.impl.UserServiceImpl;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/"));
        return handler;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authenticationManager) {


        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
        authFilter.setServerAuthenticationConverter(new ServerHttpBasicAuthenticationConverter());

        XorServerCsrfTokenRequestAttributeHandler xorHandler = new XorServerCsrfTokenRequestAttributeHandler();
        xorHandler.setTokenFromMultipartDataEnabled(true);

        return http
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(csrf -> csrf
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .csrfTokenRepository(new CookieServerCsrfTokenRepository())
                        .csrfTokenRequestHandler(xorHandler))

                //      .csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/registration").permitAll()
                        .pathMatchers("/items/**", "", "/").permitAll()

                        .anyExchange().authenticated())
                .formLogin(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler(new WebSessionServerLogoutHandler())
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .build();
    }
}
