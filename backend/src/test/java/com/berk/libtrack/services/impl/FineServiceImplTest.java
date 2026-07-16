package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.FineRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FineServiceImplTest {

    @Mock
    private FineRepository fineRepository;

    @InjectMocks
    private FineServiceImpl fineService;

    private FineEntity existingFine;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        existingFine = new FineEntity();
        existingFine.setId(1L);
        existingFine.setDaysOverdue(2);
        existingFine.setAmount(4);
        existingFine.setIsPaid(false);

        pageable = PageRequest.of(0, 10);
    }


    @Test
    void partialUpdate_Success_UpdatesProvidedFields() {
        FineEntity updateRequest = new FineEntity();
        updateRequest.setDaysOverdue(5);
        updateRequest.setAmount(10);
        updateRequest.setIsPaid(true);
        updateRequest.setPaidAt(LocalDateTime.now());

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(FineEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FineEntity updatedFine = fineService.partialUpdate(1L, updateRequest);

        assertEquals(5, updatedFine.getDaysOverdue());
        assertEquals(10, updatedFine.getAmount());
        assertTrue(updatedFine.getIsPaid());
        assertNotNull(updatedFine.getPaidAt());
        verify(fineRepository, times(1)).save(existingFine);
    }

    @Test
    void partialUpdate_Success_IgnoresNullFields() {
        FineEntity updateRequest = new FineEntity();
        updateRequest.setIsPaid(true); // Only updating the paid status

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(FineEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FineEntity updatedFine = fineService.partialUpdate(1L, updateRequest);

        assertTrue(updatedFine.getIsPaid());
        assertEquals(2, updatedFine.getDaysOverdue()); // Unchanged
        assertEquals(4, updatedFine.getAmount());      // Unchanged
    }

    @Test
    void partialUpdate_ThrowsResourceNotFoundException_WhenFineDoesNotExist() {
        FineEntity updateRequest = new FineEntity();
        when(fineRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> fineService.partialUpdate(99L, updateRequest));

        assertEquals("Non Existing Fine with id:99", exception.getMessage());
        verify(fineRepository, never()).save(any());
    }

    @Test
    void findAll_WithSearch_WhenSearchIsNull_CallsStandardFindAll() {
        Page<FineEntity> mockPage = new PageImpl<>(List.of(existingFine));
        when(fineRepository.findAll(pageable)).thenReturn(mockPage);

        Page<FineEntity> result = fineService.findAll(pageable, null);

        assertNotNull(result);
        verify(fineRepository, times(1)).findAll(pageable);
        verify(fineRepository, never()).findByLoanEntity_BookEntity_TitleContainingIgnoreCase(any(), any());
    }

    @Test
    void findAll_WithSearch_WhenSearchIsBlank_CallsStandardFindAll() {
        Page<FineEntity> mockPage = new PageImpl<>(List.of(existingFine));
        when(fineRepository.findAll(pageable)).thenReturn(mockPage);

        Page<FineEntity> result = fineService.findAll(pageable, "   ");

        assertNotNull(result);
        verify(fineRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_WithSearch_WhenSearchIsValid_CallsCustomRepositoryMethod() {
        Page<FineEntity> mockPage = new PageImpl<>(List.of(existingFine));
        String searchTerm = "Dune";
        when(fineRepository.findByLoanEntity_BookEntity_TitleContainingIgnoreCase(searchTerm, pageable)).thenReturn(mockPage);

        Page<FineEntity> result = fineService.findAll(pageable, searchTerm);

        assertNotNull(result);
        verify(fineRepository, times(1)).findByLoanEntity_BookEntity_TitleContainingIgnoreCase(searchTerm, pageable);
        verify(fineRepository, never()).findAll(pageable);
    }

    @Test
    void updateFine_SkipsPaidFines() {
        // Arrange
        FineEntity paidFine = new FineEntity();
        paidFine.setIsPaid(true);
        paidFine.setDaysOverdue(5);
        paidFine.setAmount(10);

        when(fineRepository.findByLoanEntityStatus(LoanStatus.OVERDUE)).thenReturn(List.of(paidFine));

        fineService.updateFine();

        assertEquals(5, paidFine.getDaysOverdue());
        assertEquals(10, paidFine.getAmount());
    }

    @Test
    void updateFine_CalculatesNewAmountAndDays_ForUnpaidFines() {
        LoanEntity loan = new LoanEntity();
        loan.setDueDate(LocalDateTime.now().minusDays(5));

        FineEntity unpaidFine = new FineEntity();
        unpaidFine.setIsPaid(false);
        unpaidFine.setDaysOverdue(4);
        unpaidFine.setAmount(8);
        unpaidFine.setLoanEntity(loan);

        when(fineRepository.findByLoanEntityStatus(LoanStatus.OVERDUE)).thenReturn(List.of(unpaidFine));

        fineService.updateFine();

        assertEquals(5, unpaidFine.getDaysOverdue()); // 4 + 1
        assertEquals(10, unpaidFine.getAmount());     // 5 * 2
    }

    @Test
    void save_Success() {
        when(fineRepository.save(existingFine)).thenReturn(existingFine);
        FineEntity result = fineService.save(existingFine);
        assertEquals(existingFine, result);
    }

    @Test
    void delete_Success() {
        fineService.delete(1L);
        verify(fineRepository, times(1)).deleteById(1L);
    }

    @Test
    void isExists_ReturnsTrue() {
        when(fineRepository.existsById(1L)).thenReturn(true);
        assertTrue(fineService.isExists(1L));
    }

    @Test
    void findAll_PageableOnly_Success() {
        Page<FineEntity> mockPage = new PageImpl<>(List.of(existingFine));
        when(fineRepository.findAll(pageable)).thenReturn(mockPage);
        Page<FineEntity> result = fineService.findAll(pageable);
        assertNotNull(result);
    }

    @Test
    void findOne_Success() {
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        Optional<FineEntity> result = fineService.findOne(1L);
        assertTrue(result.isPresent());
    }
}