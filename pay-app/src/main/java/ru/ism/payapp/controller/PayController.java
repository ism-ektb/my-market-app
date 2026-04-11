package ru.ism.payapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ism.payapp.api.PayControllerApi;
import ru.ism.payapp.domain.BalanceDto;
import ru.ism.payapp.domain.BayDto;
import ru.ism.payapp.service.PayService;

@RestController
@RequiredArgsConstructor
public class PayController implements PayControllerApi {

    private final PayService payService;
    /**
     * PUT /bay : Запрос на списание средств со счета пользователя
     * Запрос на списание средств со счета пользователя, в теле запроса передается id пользователя и сумма платежа
     *
     * @param bayDto   данные запроса (required)
     * @param exchange
     * @return Запрос выполнен, средства списаны (status code 200)
     * or Запрос не выполнен, средства не списаны (status code 400)
     * or Пользователь не найден (status code 404)
     * or Ошибка сервера (status code 5XX)
     */
    @Override
    public Mono<ResponseEntity<Void>> bayRequest(Mono<BayDto> bayDto, ServerWebExchange exchange) {
        return payService.bay(bayDto)
                .map(ResponseEntity::ok);
    }

    /**
     * GET /amount : Получение текущего баланса пользователя
     * Получение текущего баланса пользователя, информация передается в теле ответа и содержит id пользователя и баланс
     *
     * @param userId   Id пользователя (optional, default to 1)
     * @param exchange
     * @return Баланс найден (status code 200)
     * or Ошибка в запросе (status code 400)
     * or Отсутствует пользователь или его баланс (status code 404)
     * or Ошибка сервера (status code 5XX)
     */
    @Override
    public Mono<ResponseEntity<BalanceDto>> getBalance(Long userId, ServerWebExchange exchange) {
        return payService.getBalance(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
