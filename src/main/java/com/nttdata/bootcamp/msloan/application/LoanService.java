package com.nttdata.bootcamp.msloan.application;

import com.nttdata.bootcamp.msloan.dto.LoanDto;
import com.nttdata.bootcamp.msloan.model.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanService {

    public Flux<Loan> findAll();

    public Mono<Loan> findById(String idLoan);

    public Mono<Loan> save(LoanDto loanDto);

    public Mono<Loan> update(LoanDto loanDto, String idLoan);

    public Mono<Void> delete(String idLoan);

    public Mono<LoanDto> findMovementsByDocumentNumber(String documentNumber);

    public Flux<Loan> findLoanByDocumentNumber(String documentNumber);
}
