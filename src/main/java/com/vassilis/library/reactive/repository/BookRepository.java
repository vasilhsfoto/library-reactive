package com.vassilis.library.reactive.repository;

import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;

import com.vassilis.library.reactive.model.Book;
import reactor.core.publisher.Flux;

public interface BookRepository extends ReactiveCouchbaseRepository<Book, String> {

    @Query("#{#n1ql.selectEntity} WHERE _meta.type = 'BOOK'")
    public Flux<Book> getBooks();
}
