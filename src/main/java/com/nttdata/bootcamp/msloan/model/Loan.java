package com.nttdata.bootcamp.msloan.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class Loan model.
 * Loan microservice class Loan.
 */
@Document(collection = "Loan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {

    @Id
    private String idLoan;

    private Client client;

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

    private Double debtBalance;

    private LocalDateTime disbursementDate;

    private LocalDateTime paymentDate;

    private LocalDateTime expirationDate;
}
