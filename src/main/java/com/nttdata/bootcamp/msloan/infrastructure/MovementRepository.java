package com.nttdata.bootcamp.msloan.infrastructure;

import com.nttdata.bootcamp.msloan.config.WebClientConfig;
import com.nttdata.bootcamp.msloan.model.Movement;
import com.nttdata.bootcamp.msloan.util.Constants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MovementRepository {

    @Value("${local.property.host.ms-movement}")
    private String propertyHostMsMovement;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @CircuitBreaker(name = Constants.MOVEMENT_CB, fallbackMethod = "getDefaultMovementsByLoanNumber")
    public Flux<Movement> findMovementsByLoanNumber(String loanNumber) {
        log.info("Inicio----findMovementsByLoanNumber-------: ");
        WebClientConfig webconfig = new WebClientConfig();
        return webconfig.setUriData("http://" + propertyHostMsMovement + ":8092")
                .flatMap(d -> webconfig.getWebclient().get().uri("/api/movements/client/loanNumber/" + loanNumber).retrieve()
                        .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                        .bodyToFlux(Movement.class)
                        // .transform(it -> reactiveCircuitBreakerFactory.create("parameter-service").run(it, throwable -> Flux.just(new Movement())))
                        .collectList()
                )
                .flatMapMany(iterable -> Flux.fromIterable(iterable));
    }

    public Flux<Movement> getDefaultMovementsByLoanNumber(String creditNumber, Exception e) {
        log.info("Inicio----getDefaultMovementsByLoanNumber-------: ");
        return Flux.empty();
    }

}