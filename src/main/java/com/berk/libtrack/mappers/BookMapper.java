package com.berk.libtrack.domain.mappers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import org.springframework.stereotype.Component;


@Component
public class BookMapper {

    public BookDto toDto(BookEntity book){
        return new BookDto(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                book.getGenre(), book.getTotalCopies(), book.getAvailableCopies(), book.getCreatedAt());
    }
}
