package com.nttdata.bootcamp.msloan.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Credit {

    @Id
    private String idCredit;
    private Client client;
    private Integer creditNumber;
    private String creditType;
    private Double creditLineAmount;
    private String currency;
    private Boolean status;
    private Double balance;

    //PENDIENTE VALIDAR SI ESTA EN GRABAR
    //private Double debtBalance;
    private LocalDateTime disbursementDate;
    private LocalDateTime paymentDate;
    private LocalDateTime expirationDate;

}
