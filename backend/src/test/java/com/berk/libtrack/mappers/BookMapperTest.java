package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapper();

    @Test
    void mapTo_copiesEveryField() {
        BookEntity entity = new BookEntity();
        entity.setId(1L);
        entity.setIsbn(9781234567897L);
        entity.setTitle("Dune");
        entity.setAuthor("Herbert");
        entity.setGenre("scifi");
        entity.setTotalCopies(5);
        entity.setAvailableCopies(3);

        BookDto dto = bookMapper.mapTo(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getIsbn()).isEqualTo(9781234567897L);
        assertThat(dto.getTitle()).isEqualTo("Dune");
        assertThat(dto.getAuthor()).isEqualTo("Herbert");
        assertThat(dto.getGenre()).isEqualTo("scifi");
        assertThat(dto.getTotalCopies()).isEqualTo(5);
        assertThat(dto.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void mapFrom_copiesEveryField() {
        BookDto dto = BookDto.builder()
                .id(1L)
                .isbn(9781234567897L)
                .title("Dune")
                .author("Herbert")
                .genre("scifi")
                .totalCopies(5)
                .availableCopies(3)
                .build();

        BookEntity entity = bookMapper.mapFrom(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getIsbn()).isEqualTo(9781234567897L);
        assertThat(entity.getTitle()).isEqualTo("Dune");
        assertThat(entity.getAuthor()).isEqualTo("Herbert");
        assertThat(entity.getGenre()).isEqualTo("scifi");
        assertThat(entity.getTotalCopies()).isEqualTo(5);
        assertThat(entity.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void roundTrip_preservesValues() {
        BookEntity entity = new BookEntity();
        entity.setId(7L);
        entity.setIsbn(42L);
        entity.setTitle("Round Trip");
        entity.setAuthor("Author");
        entity.setGenre("test");
        entity.setTotalCopies(2);
        entity.setAvailableCopies(2);

        BookEntity back = bookMapper.mapFrom(bookMapper.mapTo(entity));

        assertThat(back.getId()).isEqualTo(entity.getId());
        assertThat(back.getTitle()).isEqualTo(entity.getTitle());
        assertThat(back.getAvailableCopies()).isEqualTo(entity.getAvailableCopies());
    }
}