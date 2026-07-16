package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookEntity existingBook;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        existingBook = new BookEntity();
        existingBook.setId(1L);
        existingBook.setIsbn(111111L); // Updated to Long
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");
        existingBook.setGenre("Old Genre");
        existingBook.setTotalCopies(5);
        existingBook.setAvailableCopies(5);

        pageable = PageRequest.of(0, 10);
    }


    @Test
    void partialUpdate_Success_UpdatesAllProvidedFields() {
        BookEntity updateRequest = new BookEntity();
        updateRequest.setIsbn(222222L); // Updated to Long
        updateRequest.setTitle("New Title");
        updateRequest.setAuthor("New Author");
        updateRequest.setGenre("New Genre");
        updateRequest.setTotalCopies(10);
        updateRequest.setAvailableCopies(8);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookEntity updatedBook = bookService.partialUpdate(1L, updateRequest);

        assertEquals(222222L, updatedBook.getIsbn()); // Updated assertion to Long
        assertEquals("New Title", updatedBook.getTitle());
        assertEquals("New Author", updatedBook.getAuthor());
        assertEquals("New Genre", updatedBook.getGenre());
        assertEquals(10, updatedBook.getTotalCopies());
        assertEquals(8, updatedBook.getAvailableCopies());
    }

    @Test
    void partialUpdate_Success_IgnoresNullFields() {
        BookEntity updateRequest = new BookEntity();
        // Leaving all fields null intentionally to cover the empty Optional branches

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookEntity updatedBook = bookService.partialUpdate(1L, updateRequest);

        // Assert nothing changed
        assertEquals(111111L, updatedBook.getIsbn()); // Updated assertion to Long
        assertEquals("Old Title", updatedBook.getTitle());
    }

    @Test
    void partialUpdate_ThrowsResourceNotFoundException_WhenBookDoesNotExist() {
        BookEntity updateRequest = new BookEntity();
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.partialUpdate(99L, updateRequest));
    }


    @Test
    void delete_Success_WhenNoLoanRecordsExist() {
        when(loanRepository.existsByBookEntity_Id(1L)).thenReturn(false);

        bookService.delete(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ThrowsDataIntegrityException_WhenLoanRecordsExist() {
        when(loanRepository.existsByBookEntity_Id(1L)).thenReturn(true);

        assertThrows(DataIntegrityException.class, () -> bookService.delete(1L));
        verify(bookRepository, never()).deleteById(any());
    }


    @Test
    void findAll_WithSearch_WhenSearchIsNull_CallsStandardFindAll() {
        Page<BookEntity> mockPage = new PageImpl<>(List.of(existingBook));
        when(bookRepository.findAll(pageable)).thenReturn(mockPage);

        Page<BookEntity> result = bookService.findAll(pageable, null);

        assertNotNull(result);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookRepository, never()).findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(any(), any(), any(), any());
    }

    @Test
    void findAll_WithSearch_WhenSearchIsBlank_CallsStandardFindAll() {
        Page<BookEntity> mockPage = new PageImpl<>(List.of(existingBook));
        when(bookRepository.findAll(pageable)).thenReturn(mockPage);

        // Passing a blank string to hit the `search.isBlank()` true branch
        Page<BookEntity> result = bookService.findAll(pageable, "   ");

        assertNotNull(result);
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_WithSearch_WhenSearchIsValid_CallsCustomRepositoryMethod() {
        Page<BookEntity> mockPage = new PageImpl<>(List.of(existingBook));
        String searchTerm = "Sci-Fi";
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, pageable)).thenReturn(mockPage);

        // Passing valid text to hit the false branch for the `if` statement
        Page<BookEntity> result = bookService.findAll(pageable, searchTerm);

        assertNotNull(result);
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(searchTerm, searchTerm, searchTerm, pageable);
        verify(bookRepository, never()).findAll(pageable);
    }


    @Test
    void save_Success() {
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        BookEntity result = bookService.save(existingBook);
        assertEquals(existingBook, result);
    }

    @Test
    void isExists_ReturnsTrue() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        assertTrue(bookService.isExists(1L));
    }

    @Test
    void isExists_ReturnsFalse() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        assertFalse(bookService.isExists(1L));
    }

    @Test
    void findAll_PageableOnly_Success() {
        Page<BookEntity> mockPage = new PageImpl<>(List.of(existingBook));
        when(bookRepository.findAll(pageable)).thenReturn(mockPage);

        Page<BookEntity> result = bookService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findOne_Success_ReturnsOptional() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        Optional<BookEntity> result = bookService.findOne(1L);

        assertTrue(result.isPresent());
        assertEquals(existingBook, result.get());
    }
}