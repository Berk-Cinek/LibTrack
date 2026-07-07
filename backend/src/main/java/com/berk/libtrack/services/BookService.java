package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookEntity save(BookEntity bookEntity);

    Boolean isExists(Long id);

    BookEntity partialUpdate(Long id, BookEntity bookEntity);

    void delete(Long id);

    Page<BookEntity> findAll(Pageable pageable, String search);

    Page<BookEntity> findAll(Pageable pageable);

    Optional<BookEntity> findOne(Long id);


}
