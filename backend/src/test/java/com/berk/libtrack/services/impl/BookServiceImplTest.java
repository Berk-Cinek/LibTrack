package com.berk.libtrack.services.impl;

import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    // Test 20
    @Test
    void delete_rejectsBookWithLoans() {
        when(loanRepository.existsByBookEntity_Id(7L)).thenReturn(true);

        assertThatThrownBy(() -> bookService.delete(7L))
                .isInstanceOf(DataIntegrityException.class)
                .hasMessageContaining("loan records");

        verify(bookRepository, never()).deleteById(anyLong());
    }
}