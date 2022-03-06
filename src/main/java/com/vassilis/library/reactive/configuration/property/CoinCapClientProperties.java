package com.vassilis.library.reactive.configuration.property;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Configuration
@ConfigurationProperties("coin-cap-client")
@Validated
@Data
public class CoinCapClientProperties {
    @NotBlank
    private String url;
}
