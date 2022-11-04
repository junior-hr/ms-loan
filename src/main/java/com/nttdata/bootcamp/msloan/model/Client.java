package com.nttdata.bootcamp.msloan.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class Client.
 * Loan microservice class Client.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {

    @Id
    private String idClient;
    private String clientType;
    private String documentType;
    private String documentNumber;

}