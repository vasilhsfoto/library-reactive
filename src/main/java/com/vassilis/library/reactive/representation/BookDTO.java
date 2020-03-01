package com.vassilis.library.reactive.representation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class BookDTO {
    @Data
    public static class AuthorDTO {
        @NotNull
        private String name;

        private LocalDate birthDate;
    }

    private String id;

    @NotNull
    private String name;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private Long numOfPages;

    @NotEmpty
    private List<AuthorDTO> authors;

    @NotNull
    private Double price;
}
