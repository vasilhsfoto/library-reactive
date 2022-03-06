package com.vassilis.library.reactive.representation;

import lombok.Data;

@Data
public class RateDto {
    private String id;
    private String symbol;
    private String currencySymbol;
    private String rateUsd;
}
