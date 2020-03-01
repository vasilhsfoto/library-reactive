package com.vassilis.library.reactive.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooksDTO {
    private List<BookDTO> books;
}
