package com.mihail.baciu.account.transfer.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
public class AccountTransferRequestDto {

    @NotNull(message = "sourceAccountId is mandatory")
    private Long sourceAccountId;
    @NotNull(message = "destinationAccountId is mandatory")
    private Long destinationAccountId;
    @NotNull(message = "amount is mandatory")
    @DecimalMin(value = "1.0", message = "Please enter a valid amount")
    private Double amount;
}
