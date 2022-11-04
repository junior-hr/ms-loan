package com.nttdata.bootcamp.msloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Class MsLoanApplication Main.
 * Loan microservice class MsLoanApplication.
 */
@SpringBootApplication
@EnableEurekaClient
public class MsLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsLoanApplication.class, args);
    }

}