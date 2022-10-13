package com.mihail.baciu.account.transfer.service;

import com.mihail.baciu.account.transfer.dto.AccountTransferRequestDto;
import com.mihail.baciu.account.transfer.dto.ExchangeRatesApiDto;
import com.mihail.baciu.account.transfer.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.mihail.baciu.account.transfer.constants.Constants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class AccountTransferService {

    private final AccountRepository accountRepository;
    private final WebClient webClient;

    @Value("${external.api.key}")
    private String apiKey;

    @Value("${external.api.url}")
    private String apiUrl;

    public AccountTransferService(AccountRepository accountRepository, WebClient webClient) {
        this.accountRepository = accountRepository;
        this.webClient = WebClient.create();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String doTransfer(AccountTransferRequestDto accountTransferRequestDto) {

        var sourceAccount = accountRepository.getAccountByOwnerId(accountTransferRequestDto.getSourceAccountId());
        var destinationAccount = accountRepository.getAccountByOwnerId(accountTransferRequestDto.getDestinationAccountId());
        if (Objects.isNull(sourceAccount)) {
            throw new ResponseStatusException(NOT_FOUND, DEBIT_ACC_NOT_FOUND);
        }
        if (Objects.isNull(destinationAccount)) {
            throw new ResponseStatusException(NOT_FOUND, CREDIT_ACC_NOT_FOUND);
        }
        if (sourceAccount.getBalance() < accountTransferRequestDto.getAmount()) {
            throw new ResponseStatusException(PRECONDITION_REQUIRED, INSUFFICIENT_BALANCE);
        }
        sourceAccount.setBalance(sourceAccount.getBalance() - accountTransferRequestDto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + getFxAmount(sourceAccount.getCurrency(), destinationAccount.getCurrency(), accountTransferRequestDto.getAmount()));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        return RESPONSE_MESSAGE;
    }

    private Double getExchangeRate(String sourceAccountCurrency, String destinationAccountCurrency) {

        var url = apiUrl + "?base=" + sourceAccountCurrency + "&symbols=" + destinationAccountCurrency;

        return webClient.get().uri(url)
                .header("apikey", apiKey)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ExchangeRatesApiDto>() {
                })
                .onErrorMap(error -> new ResponseStatusException(NOT_FOUND, "Error in getting FX rate from external api", error))
                .block()
                .getRates()
                .get(destinationAccountCurrency);

    }

    private Double getFxAmount(String sourceAccountCurrency, String destinationAccountCurrency, Double amount) {
        return amount * getExchangeRate(sourceAccountCurrency, destinationAccountCurrency);
    }
}


