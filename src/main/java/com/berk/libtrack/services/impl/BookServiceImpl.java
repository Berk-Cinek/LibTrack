package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.services.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookEntity save(BookEntity bookEntity) {
       return bookRepository.save(bookEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return bookRepository.existsById(id);
    }

    @Override
    public BookEntity partialUpdate(Long id, BookEntity bookEntity) {
        bookEntity.setId(id);

        return bookRepository.findById(id).map(existingBook ->{
            Optional.ofNullable(bookEntity.getIsbn()).ifPresent(existingBook::setIsbn);
            Optional.ofNullable(bookEntity.getTitle()).ifPresent(existingBook::setTitle);
            Optional.ofNullable(bookEntity.getAuthor()).ifPresent(existingBook::setAuthor);
            Optional.ofNullable(bookEntity.getGenre()).ifPresent(existingBook::setGenre);
            Optional.ofNullable(bookEntity.getTotalCopies()).ifPresent(existingBook::setTotalCopies);
            Optional.ofNullable(bookEntity.getAvailableCopies()).ifPresent(existingBook::setAvailableCopies);
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Author does not exist"));
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
