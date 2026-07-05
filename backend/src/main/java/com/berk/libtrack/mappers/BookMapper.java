package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper implements Mapper<BookEntity, BookDto>{


    @Override
    public BookDto mapTo(BookEntity bookEntity) {
        return BookDto.builder()
                .id(bookEntity.getId())
                .isbn(bookEntity.getIsbn())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .genre(bookEntity.getGenre())
                .totalCopies(bookEntity.getTotalCopies())
                .availableCopies(bookEntity.getAvailableCopies())
                .createdAt(bookEntity.getCreatedAt())
                .build();
    }

    @Override
    public BookEntity mapFrom(BookDto bookDto) {
        return BookEntity.builder()
                .id(bookDto.getId())
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .genre(bookDto.getGenre())
                .totalCopies(bookDto.getTotalCopies())
                .availableCopies(bookDto.getAvailableCopies())
                .createdAt(bookDto.getCreatedAt())
                .build();
    }
}
