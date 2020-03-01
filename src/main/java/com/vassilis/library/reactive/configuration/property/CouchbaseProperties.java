package com.vassilis.library.reactive.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@ConfigurationProperties("couchbase")
@Validated
@Data
public class CouchbaseProperties {

    @NotBlank
    private String bucketName;
    @NotEmpty
    private List<String> hosts;
    @NotEmpty
    private String username;
    @NotNull
    private String password;
}
