package com.vassilis.library.reactive.configuration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.repository.auditing.EnableCouchbaseAuditing;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

import com.vassilis.library.reactive.configuration.property.CouchbaseProperties;
import lombok.Data;

@Configuration
@EnableCouchbaseAuditing
@EnableReactiveCouchbaseRepositories
@Data
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties couchbaseProperties;

    @Override
    public CustomConversions customConversions() {
        return new CouchbaseCustomConversions(List.of(LocalDateToString.INSTANCE, StringToLocalDate.INSTANCE));
    }

    @Override
    public String getConnectionString() {
        return couchbaseProperties.getHost();
    }

    @Override
    public String getUserName() {
        return couchbaseProperties.getUsername();
    }

    @Override
    public String getPassword() {
        return couchbaseProperties.getPassword();
    }

    @Override
    public String getBucketName() {
        return couchbaseProperties.getBucketName();
    }

    @WritingConverter
    public enum LocalDateToString implements Converter<LocalDate, String> {
        INSTANCE;

        @Override
        public String convert(LocalDate localDate) {
            return localDate.toString();
        }
    }

    @ReadingConverter
    public enum StringToLocalDate implements Converter<String, LocalDate> {
        INSTANCE;

        @Override
        public LocalDate convert(String dateStr) {
            return LocalDate.parse(dateStr);
        }
    }
}
