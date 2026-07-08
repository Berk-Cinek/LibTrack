package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.services.BookService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final LoanRepository loanRepository;
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @CachePut(value = "BOOK_CACHE", key = "#result.id()" )
    public BookEntity save(BookEntity bookEntity) {
        return bookRepository.save(bookEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return bookRepository.existsById(id);
    }

    @Override
    @CachePut(value = "BOOK_CACHE", key = "#result.id()" )
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
        }).orElseThrow(() -> new ResourceNotFoundException("Book does not exist with id:" + id));
    }

    @Override
    @CacheEvict(value = "BOOK_CACHE", key = "#id")
    public void delete(Long id) {
        if(loanRepository.existsByBookEntity_Id(id)){
            throw new DataIntegrityException(
                    "cannot delete this book, it has loan records, consider making it unavailable");
        }

        bookRepository.deleteById(id);

    }

    @Override
    public Page<BookEntity> findAll(Pageable pageable, String search) {
        if (search == null || search.isBlank()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        search, search, search, pageable);
    }

    @Override
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "BOOK_CACHE", key = "#id")
    public Optional<BookEntity> findOne(Long id) {
        return bookRepository.findById(id);
    }


}
