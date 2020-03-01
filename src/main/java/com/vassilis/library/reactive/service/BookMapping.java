package com.vassilis.library.reactive.service;

import com.vassilis.library.reactive.model.Book;
import com.vassilis.library.reactive.model.Book.Author;
import com.vassilis.library.reactive.model.Meta;
import com.vassilis.library.reactive.representation.BookDTO;
import com.vassilis.library.reactive.representation.BookDTO.AuthorDTO;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

// TODO: Replace it with the usage of lombok project
@Service
public class BookMapping {

    public Book toBook(BookDTO bookDTO, String libraryId) {
        Book book = new Book();

        book.setLibraryId(libraryId);
        book.setName(bookDTO.getName());
        book.setNumOfPages(bookDTO.getNumOfPages());
        book.setPrice(bookDTO.getPrice());
        book.setPublicationDate(bookDTO.getPublicationDate());

        book.setAuthors(bookDTO.getAuthors().stream()
                .map(this::toAuthor)
                .collect(Collectors.toList())
        );

        Meta meta = new Meta();
        meta.setType(Book.TYPE);
        book.setMeta(meta);

        return book;
    }

    public BookDTO toBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();

        bookDTO.setId(book.getId());
        bookDTO.setName(book.getName());
        bookDTO.setNumOfPages(book.getNumOfPages());
        bookDTO.setPrice(book.getPrice());
        bookDTO.setPublicationDate(book.getPublicationDate());

        bookDTO.setAuthors(book.getAuthors().stream()
                .map(this::toAuthorDTO)
                .collect(Collectors.toList())
        );
        return bookDTO;
    }

    public Book updateBook(BookDTO bookDTO, Book existingBook) {
        Book bookToSave = new Book();

        bookToSave.setId(existingBook.getId());
        bookToSave.setPublicationDate(bookDTO.getPublicationDate());
        bookToSave.setPrice(bookDTO.getPrice());
        bookToSave.setNumOfPages(bookDTO.getNumOfPages());
        bookToSave.setName(bookDTO.getName());
        bookToSave.setLibraryId(existingBook.getLibraryId());
        bookToSave.setAuthors(bookDTO.getAuthors().stream()
                .map(this::toAuthor)
                .collect(Collectors.toList()));
        bookToSave.setMeta(existingBook.getMeta());
        return bookToSave;
    }

    public Author toAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        author.setBirthDate(author.getBirthDate());
        return author;
    }

    public AuthorDTO toAuthorDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();

        authorDTO.setName(author.getName());
        authorDTO.setBirthDate(author.getBirthDate());
        return authorDTO;
    }
}
