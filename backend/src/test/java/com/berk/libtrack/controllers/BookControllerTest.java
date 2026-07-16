package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.mappers.Mapper;
import com.berk.libtrack.security.JwtAuthFilter;
import com.berk.libtrack.services.BookService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = BookContorller.class,
        excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {
    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private Mapper<BookEntity, BookDto> bookMapper;

    @MockitoBean
    private OpenAPI openApi;

    private BookDto bookDto(long id) {
        return BookDto.builder().id(id).build();
    }

    @Test
    void createBook_returns201() {
        BookEntity entity = mock(BookEntity.class);
        BookEntity saved = mock(BookEntity.class);

        when(bookMapper.mapFrom(any())).thenReturn(entity);
        when(bookService.save(entity)).thenReturn(saved);
        when(bookMapper.mapTo(saved)).thenReturn(bookDto(1L));

        assertThat(mvc.post().uri("/books")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("{}"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(1);

        verify(bookService).save(entity);
    }


    @Test
    void listBooks_returns200() {
        BookEntity entity = mock(BookEntity.class);
        Page<BookEntity> page = new PageImpl<>(List.of(entity));

        when(bookService.findAll(any(Pageable.class))).thenReturn(page);
        when(bookMapper.mapTo(entity)).thenReturn(bookDto(1L));

        assertThat(mvc.get().uri("/books"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(1);
    }

    @Test
    void listBooksAdmin_returns200() {
        BookEntity entity = mock(BookEntity.class);
        Page<BookEntity> page = new PageImpl<>(List.of(entity));

        when(bookService.findAll(any(Pageable.class), eq("foo"))).thenReturn(page);
        when(bookMapper.mapTo(entity)).thenReturn(bookDto(2L));

        assertThat(mvc.get().uri("/books/admin?search=foo"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(2);
    }

    @Test
    void getById_whenExists_returns200() {
        BookEntity entity = mock(BookEntity.class);
        when(bookService.isExists(1L)).thenReturn(true);
        when(bookService.findOne(1L)).thenReturn(Optional.of(entity));
        when(bookMapper.mapTo(entity)).thenReturn(bookDto(1L));

        assertThat(mvc.get().uri("/books/{id}", 1L))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void getById_whenNotExists_returns404() {
        when(bookService.isExists(99L)).thenReturn(false);

        assertThat(mvc.get().uri("/books/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void fullUpdate_whenExists_returns200() {
        BookEntity entity = mock(BookEntity.class);
        BookEntity saved = mock(BookEntity.class);

        when(bookService.isExists(1L)).thenReturn(true);
        when(bookMapper.mapFrom(any())).thenReturn(entity);
        when(bookService.save(entity)).thenReturn(saved);
        when(bookMapper.mapTo(saved)).thenReturn(bookDto(1L));

        assertThat(mvc.put().uri("/books/{id}", 1L)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void fullUpdate_whenNotExists_returns404() {
        when(bookService.isExists(99L)).thenReturn(false);

        assertThat(mvc.put().uri("/books/{id}", 99L)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void partialUpdate_returns200() {
        BookEntity entity = mock(BookEntity.class);
        BookEntity updated = mock(BookEntity.class);

        when(bookMapper.mapFrom(any())).thenReturn(entity);
        when(bookService.partialUpdate(eq(1L), any())).thenReturn(updated);
        when(bookMapper.mapTo(updated)).thenReturn(bookDto(1L));

        assertThat(mvc.patch().uri("/books/{id}", 1L)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void deleteBook_whenExists_returns204() {
        when(bookService.isExists(1L)).thenReturn(true);

        assertThat(mvc.delete().uri("/books/{id}", 1L))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(bookService).delete(1L);
    }

    @Test
    void deleteBook_whenNotExists_returns404() {
        when(bookService.isExists(99L)).thenReturn(false);

        assertThat(mvc.delete().uri("/books/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}