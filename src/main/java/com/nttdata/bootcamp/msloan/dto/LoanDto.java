package com.nttdata.bootcamp.msloan.dto;

import com.nttdata.bootcamp.msloan.exception.ResourceNotFoundException;
import com.nttdata.bootcamp.msloan.model.Client;
import com.nttdata.bootcamp.msloan.model.Loan;
import com.nttdata.bootcamp.msloan.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@ToString
public class LoanDto {

    private String idLoan;

    private Integer documentNumber;

    @NotNull(message = "no debe estar nulo")
    private Integer loanNumber;

    @NotEmpty(message = "no debe estar vacío")
    private String loanType;

    @NotNull(message = "no debe estar nulo")
    private Double loanAmount;

    @NotEmpty(message = "no debe estar vacío")
    private String currency;

    @NotNull(message = "no debe estar nulo")
    private Integer numberQuotas;

    private String status;

    //private Double amountOfDebt;

    private Double debtBalance;

    private LocalDateTime disbursementDate;

    private LocalDateTime paymentDate;

    private LocalDateTime expirationDate;

    private List<Movement> movements;

    public Mono<Boolean> validateFields() {
        log.info("validateFields-------: " );
        return Mono.when(validateLoanType())
                .then(Mono.just(true));
    }

    public Mono<Boolean> validateLoanType(){
        log.info("Inicio validateLoanType-------: " );
        return Mono.just(this.getLoanType()).flatMap( ct -> {
            Boolean isOk = false;
            if(this.getLoanType().equalsIgnoreCase("Personal")){ //Prestamo personal.
                isOk = true;
            }
            else if(this.getLoanType().equalsIgnoreCase("Business")){ //Prestamo Empresarial.
                isOk = true;
            }else{
                return Mono.error(new ResourceNotFoundException("Tipo Prestamo", "LoanType", this.getLoanType()));
            }
            log.info("Fin validateLoanType-------: " );
            return Mono.just(isOk);
        });
    }

    public Mono<Loan> mapperToLoan(Client client) {
        log.info("Inicio MapperToLoan-------: " );
        Loan loan = Loan.builder()
                //.idLoan(this.getIdLoan())
                .client(client)
                .loanNumber(this.getLoanNumber())
                .loanType(this.getLoanType())
                .loanAmount(this.getLoanAmount())
                .currency(this.getCurrency())
                .numberQuotas(this.getNumberQuotas())
                .status(this.getStatus())
                .debtBalance(this.getDebtBalance())
                .disbursementDate(this.getDisbursementDate())
                .paymentDate(this.getPaymentDate())
                .expirationDate(this.getExpirationDate())
                .build();
        log.info("Fin MapperToLoan-------: " );
        return Mono.just(loan);
    }

}
