package com.mihail.baciu.account.transfer.controller;

import com.mihail.baciu.account.transfer.dto.AccountTransferRequestDto;
import com.mihail.baciu.account.transfer.service.AccountTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class AccountTransferController {

    private final AccountTransferService accountTransferService;


    @PostMapping("/account-transfer")
    public ResponseEntity<String> makeTransfer(@RequestBody @Validated AccountTransferRequestDto request) {

        return new ResponseEntity<>(accountTransferService.doTransfer(request), OK);
    }
}
