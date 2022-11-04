package com.nttdata.bootcamp.msloan.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.nttdata.bootcamp.msloan.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class LoanProducer.
 * Loan microservice class LoanProducer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoanProducer {

    private final KafkaTemplate<String, Loan> kafkaTemplate;

    @Value(value = "${spring.kafka.topic.loan.name}")
    private String topic;

    public void sendMessage(Loan loan) {

        ListenableFuture<SendResult<String, Loan>> future = kafkaTemplate.send(this.topic, loan);
        log.info("Prueba:: Mensaje enviado");
        future.addCallback(new ListenableFutureCallback<SendResult<String, Loan>>() {
            @Override
            public void onSuccess(SendResult<String, Loan> result) {
                log.info("Message {} has been sent ", loan);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Something went wrong with the balanceModel {} ", loan);
            }
        });
    }
}
