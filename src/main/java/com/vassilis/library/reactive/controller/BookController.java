package com.vassilis.library.reactive.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vassilis.library.reactive.exception.WebAppException;
import com.vassilis.library.reactive.model.Book;
import com.vassilis.library.reactive.repository.BookRepository;
import com.vassilis.library.reactive.representation.BookDTO;
import com.vassilis.library.reactive.representation.BooksDTO;
import com.vassilis.library.reactive.service.BookMapping;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/libraries/{libraryId}/books")
@Data
@Slf4j
public class BookController {

    private final BookRepository bookRepository;
    private final BookMapping bookMapping;
    private final Scheduler blockingHttpScheduler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<BooksDTO>> getBooks(@PathVariable String libraryId) {
        return bookRepository.getBooks()
                .take(2)
                .map(bookMapping::toBookDTO)
                .collectList() // This is not a blocking operation! it works
                .map(books -> ResponseEntity.ok(new BooksDTO(books)))
                .log();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookDTO>> createBook(@PathVariable String libraryId,
                                                    @Valid @RequestBody Mono<BookDTO> bookMono) {
        return bookMono.map(bookDTO -> bookMapping.toBook(bookDTO, libraryId))
                .flatMap(bookRepository::save)
                .map(bookMapping::toBookDTO)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{bookId}")
    public Mono<ResponseEntity<BookDTO>> getBookById(@PathVariable String libraryId, @PathVariable String bookId) {
        if (libraryId.equals("2")) {
            Mono.error(new RuntimeException("Library does not exist"));
        }

        return bookRepository.findById(bookId)
                .map(bookMapping::toBookDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new WebAppException(String.format("Book with id %s not found", bookId),
                        HttpStatus.NOT_FOUND)));
    }

    @PutMapping("/{bookId}")
    public Mono<ResponseEntity<BookDTO>> updateBook(@PathVariable String libraryId,
                                                    @PathVariable String bookId,
                                                    @Valid @RequestBody Mono<BookDTO> bookDTOMono) {
        Mono<BookDTO> bookIdMonoValidation = bookDTOMono.flatMap(bookDTO -> {
            String id = bookDTO.getId();
            if (!Objects.equals(id, bookId)) {
                return Mono.error(new WebAppException("Book id should be the same with the one in the path",
                        HttpStatus.UNPROCESSABLE_ENTITY));
            }
            return Mono.just(bookDTO);
        });

        Mono<Book> existingBookMono = bookRepository.findById(bookId);

        return existingBookMono.zipWith(bookIdMonoValidation,
                        (existingBook, bookDTO) -> bookMapping.updateBook(bookDTO, existingBook))
                .flatMap(bookRepository::save)
                .map(bookMapping::toBookDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new WebAppException(String.format("Book with id %s not found", bookId),
                        HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<Void>> deleteBook(String bookId) {
        return bookRepository.existsById(bookId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new WebAppException(String.format("Book with id %s not found", bookId),
                                HttpStatus.NOT_FOUND));
                    }

                    return bookRepository
                            .deleteById(bookId)
                            .then(Mono.just(ResponseEntity.ok().build()));
                });
    }
}
