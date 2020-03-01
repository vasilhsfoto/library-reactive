package com.vassilis.library.reactive.repository;

import com.vassilis.library.reactive.model.Book;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BookRepository extends ReactiveCouchbaseRepository<Book, String> {

    @Query("#{#n1ql.selectEntity} WHERE _meta.type = 'BOOK'")
    public Flux<Book> getBooks();
}
