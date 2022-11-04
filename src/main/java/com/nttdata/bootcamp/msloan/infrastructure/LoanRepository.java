package com.nttdata.bootcamp.msloan.infrastructure;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bootcamp.msloan.dto.LoanDto;
import com.nttdata.bootcamp.msloan.model.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class LoanRepository.
 * Loan microservice class LoanRepository.
 */
public interface LoanRepository extends ReactiveMongoRepository<Loan, String> {

    @Query(value = "{'client.documentNumber' : ?0, loanType: ?1 }")
    Flux<Loan> findByLoanClient(String documentNumber, String loanType);

    @Query(value = "{'client.documentNumber' : ?0}")
    Mono<LoanDto> findByDocumentNumber(String documentNumber);

    @Query(value = "{'client.documentNumber' : ?0}")
    Flux<Loan> findByLoanOfDocumentNumber(String documentNumber);
}