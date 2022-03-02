package com.vassilis.library.reactive.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("couchbase")
@Validated
@Data
public class CouchbaseProperties {

    @NotBlank
    private String bucketName;
    @NotBlank
    private String host;
    @NotEmpty
    private String username;
    @NotNull
    private String password;
}
