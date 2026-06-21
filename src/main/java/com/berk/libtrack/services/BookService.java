package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.BookEntity;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookEntity save(BookEntity bookEntity);

    Boolean isExists(Long id);

    BookEntity partialUpdate(Long id, BookEntity bookEntity);

    void delete(Long id);

    List<BookEntity> findAll();

    Optional<BookEntity> findOne(Long id);
}
