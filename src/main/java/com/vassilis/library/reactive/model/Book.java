package com.vassilis.library.reactive.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Book {
    public static final String TYPE = "BOOK";

    @Data
    public static class Author {
        @Field
        @NotNull
        private String name;

        @Field
        private LocalDate birthDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    @Field("_meta")
    private Meta meta;

    @Field
    @NotNull
    private String name;

    @Field
    private LocalDate publicationDate;

    @Field
    private Long numOfPages;

    @Field
    @NotEmpty
    private List<Author> authors;

    @Field
    @NotNull
    private Double price;

    @Field
    private String libraryId;
}
