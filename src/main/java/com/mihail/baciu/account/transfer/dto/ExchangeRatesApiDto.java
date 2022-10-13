package com.mihail.baciu.account.transfer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRatesApiDto {

    private Map<String,Double> rates;
}
