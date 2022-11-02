package com.nttdata.bootcamp.msloan.application;

import com.nttdata.bootcamp.msloan.dto.LoanDto;
import com.nttdata.bootcamp.msloan.exception.ResourceNotFoundException;
import com.nttdata.bootcamp.msloan.infrastructure.ClientRepository;
import com.nttdata.bootcamp.msloan.infrastructure.CreditRepository;
import com.nttdata.bootcamp.msloan.infrastructure.LoanRepository;
import com.nttdata.bootcamp.msloan.infrastructure.MovementRepository;
import com.nttdata.bootcamp.msloan.model.Client;
import com.nttdata.bootcamp.msloan.model.Loan;
import com.nttdata.bootcamp.msloan.util.Constants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Override
    public Flux<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Mono<Loan> findById(String idLoan) {
        return Mono.just(idLoan)
                .flatMap(loanRepository::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdPrestamo", idLoan)));
    }

    @Override
    public Mono<Loan> save(LoanDto loanDto) {

        return clientRepository.findClientByDni(String.valueOf(loanDto.getDocumentNumber()))
                .flatMap(client -> {
                    return validateNumberClientLoan(client, loanDto, "save")
                            .flatMap(o -> {
                                log.info("sg validateNumberClientLoan-------: " + o.toString());
                                return loanDto.validateFields()
                                        .flatMap(at -> {
                                            if (at.equals(true)) {
                                                return loanDto.mapperToLoan(client)
                                                        .flatMap(ba -> {
                                                            log.info("sg MapperToLoan-------: ");
                                                            return loanRepository.save(ba);
                                                        });
                                            } else {
                                                return Mono.error(new ResourceNotFoundException("Tipo Prestamo", "LoanType", loanDto.getLoanType()));
                                            }
                                        });
                            });
                });
    }

    @Override
    public Mono<Loan> update(LoanDto loanDto, String idLoan) {
        return clientRepository.findClientByDni(String.valueOf(loanDto.getDocumentNumber()))
                .flatMap(client -> {
                    return validateNumberClientLoan(client, loanDto, "update").flatMap(o -> {
                        return loanDto.validateFields()
                                .flatMap(at -> {
                                    if (at.equals(true)) {
                                        return loanRepository.findById(idLoan)
                                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "idLoan", idLoan)))
                                                .flatMap(x -> {
                                                    x.setClient(client);
                                                    x.setLoanNumber(loanDto.getLoanNumber());
                                                    x.setLoanType(loanDto.getLoanType());
                                                    x.setLoanAmount(loanDto.getLoanAmount());
                                                    x.setCurrency(loanDto.getCurrency());
                                                    x.setNumberQuotas(loanDto.getNumberQuotas());
                                                    x.setStatus(loanDto.getStatus());
                                                    x.setDebtBalance(loanDto.getDebtBalance());
                                                    x.setDisbursementDate(loanDto.getDisbursementDate());
                                                    x.setPaymentDate(loanDto.getPaymentDate());
                                                    x.setExpirationDate(loanDto.getExpirationDate());
                                                    return loanRepository.save(x);
                                                });
                                    } else {
                                        return Mono.error(new ResourceNotFoundException("Tipo Prestamo", "LoanType", loanDto.getLoanType()));
                                    }
                                });
                    });
                });
    }

    @Override
    public Mono<Void> delete(String idLoan) {
        return loanRepository.findById(idLoan)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prestamo", "IdLoan", idLoan)))
                .flatMap(loanRepository::delete);
    }


    public Mono<Boolean> validateNumberClientLoan(Client client, LoanDto loanDto, String method) {
        log.info("Inicio validateNumberClientLoan-------: ");
        Boolean isOk = false;
        if (client.getClientType().equals("Personal")) {
            if (method.equals("save")) {
                Flux<Loan> list = loanRepository.findByLoanClient(client.getDocumentNumber(), loanDto.getLoanType());
                return list.count().flatMap(cant -> {
                    log.info("1 Personal cantidad : ", cant);
                    if (cant >= 1) {
                        log.info("2 Personal cantidad : ", cant);
                        return Mono.error(new ResourceNotFoundException("Cliente ", client.getClientType()));
                    } else {
                        log.info("3 Personal cantidad : ", cant);
                        log.info("Cliente Personal no tiene prestamo");
                        return validateLoanDebt(client.getDocumentNumber(), "Personal").flatMap(vl ->{
                            if (vl.equals(true)){
                                return validateCreditCardDebt(client.getDocumentNumber(), "Personal");
                            }else{
                                return Mono.just(false);
                            }
                        });
                    }
                });
            } else {
                return Mono.just(true);
            }
        } else if (client.getClientType().equals("Business")) {
            if (method.equals("save")) {
                Flux<Loan> list = loanRepository.findByLoanClient(client.getDocumentNumber(), loanDto.getLoanType());
                return list.count().flatMap(cant -> {
                    log.info("1 Business cantidad : ", cant);

                    return validateLoanDebt(client.getDocumentNumber(), "Business").flatMap( vl ->{
                        if (vl.equals(true)){
                            return validateCreditCardDebt(client.getDocumentNumber(), "Business");
                        }else{
                            return Mono.just(false);
                        }
                    });


                });
            } else {
                return Mono.just(true);
            }
        } else {
            return Mono.error(new ResourceNotFoundException("Tipo Cliente", "ClientType", client.getClientType()));
        }
    }

    @Override
    public Mono<LoanDto> findMovementsByDocumentNumber(String documentNumber) {
        log.info("Inicio----findMovementsByDocumentNumber-------: ");
        log.info("Inicio----findMovementsByDocumentNumber-------documentNumber : " + documentNumber);
        return loanRepository.findByDocumentNumber(documentNumber)
                .flatMap(d -> {
                    log.info("Inicio----findMovementsByCreditNumber-------: ");
                    log.info("Inicio----findMovementsByCreditNumber-------: " + d.toString());
                    log.info("Inicio----findMovementsByCreditNumber-------: " + d.getLoanNumber().toString() );
                    return movementRepository.findMovementsByLoanNumber(d.getLoanNumber().toString())
                            .collectList()
                            .flatMap(m -> {
                                log.info("----findMovementsByLoanNumber setMovements-------: ");
                                d.setMovements(m);
                                return Mono.just(d);
                            });
                });
    }

    @Override
    public Flux<Loan> findLoanByDocumentNumber(String documentNumber) {
        log.info("Inicio----findLoanByDocumentNumber-------: ");
        log.info("Inicio----findLoanByDocumentNumber-------documentNumber : " + documentNumber);
        return loanRepository.findByLoanOfDocumentNumber(documentNumber);
    }

    //Si tiene deuda return false sino true
    public Mono<Boolean> validateCreditCardDebt(String documentNumber, String creditType) {

        log.info("Inicio----validateCreditCardDebt-------: ");
        log.info("Inicio----validateCreditCardDebt-------documentNumber : " + documentNumber);
        LocalDateTime datetime = LocalDateTime.now();
        return creditRepository.findCreditsByDocumentNumber(documentNumber)
                .collectList()
                .flatMap(c -> {
                    log.info("sg----findCreditsByDocumentNumber-------: ");
                    log.info("sg----findCreditsByDocumentNumber-------: " + c.toString());
                    if (creditType.equals("Personal")) {
                        if (c.size() == Constants.ZERO || c == null) {
                            log.info("Inicio----validateCreditCardDebt-------if1: ");
                            return Mono.just(true);
                        } else {
                            log.info("Inicio----validateCreditCardDebt-------else1: ");
                            if (datetime.isBefore(c.get(0).getExpirationDate())) {
                                log.info("Inicio----validateCreditCardDebt-------if2: ");
                                return Mono.just(true);//No se vence
                            } else {
                                log.info("Inicio----validateCreditCardDebt-------else2: ");
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    } else if (creditType.equals("Business")) {
                        if (c == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(c.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    }else{
                        return Mono.just(false);
                    }
                });
    }

    //Si tiene deuda retorna false
    public Mono<Boolean> validateLoanDebt(String documentNumber, String loanType) {

        log.info("Inicio----validateLoanDebt-------: ");
        log.info("Inicio----validateLoanDebt-------documentNumber : " + documentNumber);
        LocalDateTime datetime = LocalDateTime.now();
        return loanRepository.findByLoanOfDocumentNumber(documentNumber)
                .collectList()
                .flatMap(l -> {
                    if (loanType.equals("Personal")) {
                        if (l.size() == Constants.ZERO || l == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(l.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    } else if (loanType.equals("Business")) {
                        if (l == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(l.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    }
                    return Mono.just(true);
                });
    }

}
