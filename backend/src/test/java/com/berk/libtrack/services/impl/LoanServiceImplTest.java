package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.exceptions.BorrowingNotAllowedException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.repositories.MemberRepository;
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
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Pageable pageable;
    private MemberEntity standardMember;
    private BookEntity standardBook;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        standardMember = new MemberEntity();
        standardMember.setId(1L);

        standardBook = new BookEntity();
        standardBook.setId(5L);
        standardBook.setAvailableCopies(10);
    }

    @Test
    void loanCreate_rejectsMemberWithThreeActiveLoans(){
        LoanEntity request = new LoanEntity();
        request.setMemberEntity(standardMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(standardMember));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(standardBook));

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.ACTIVE)).thenReturn(3L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("3 active loans");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_acceptMemberWithLessThenThreeLoans(){
        LoanEntity request = new LoanEntity();
        request.setMemberEntity(standardMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(standardMember));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(standardBook));

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.ACTIVE)).thenReturn(2L);

        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.loanCreate(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(result.getBorrowedAt()).isNotNull();
        assertThat(standardBook.getAvailableCopies()).isEqualTo(9);

        verify(loanRepository).save(any());
    }

    @Test
    void loanCreate_rejectMemberWithOverdueBook(){
        LoanEntity request = new LoanEntity();
        request.setMemberEntity(standardMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(standardMember));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(standardBook));

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.ACTIVE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(1L);

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(1L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("overdue books cannot borrow new ones");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_rejectWhenBookOutOfStock(){
        standardBook.setAvailableCopies(0);

        LoanEntity request = new LoanEntity();
        request.setMemberEntity(standardMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(standardMember));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(standardBook));

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.ACTIVE)).thenReturn(0L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("No available copies");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_succeedsWithLastCopy(){
        standardBook.setAvailableCopies(1);

        LoanEntity request = new LoanEntity();
        request.setMemberEntity(standardMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(standardMember));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(standardBook));

        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(standardMember, LoanStatus.ACTIVE)).thenReturn(0L);

        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.loanCreate(request);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(standardBook.getAvailableCopies()).isZero();
        verify(loanRepository).save(any());
    }

    @Test
    void loanCreate_rejectUnknownMember(){
        MemberEntity unknownMember = new MemberEntity();
        unknownMember.setId(99L);

        LoanEntity request = new LoanEntity();
        request.setMemberEntity(unknownMember);
        request.setBookEntity(standardBook);

        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanPartialUpdate_returnStockWhenMarkedReturned(){
        standardBook.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.ACTIVE);
        existingLoan.setReturnedAt(null);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(standardBook.getAvailableCopies()).isEqualTo(8);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();

        verify(bookRepository).save(standardBook);
    }

    @Test
    void loanPartialUpdate_doesNotDoubleIncrementWhenAlreadyReturned(){
        standardBook.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(LocalDateTime.now().minusDays(1));

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(standardBook.getAvailableCopies()).isEqualTo(7);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void loanPartialUpdate_decrementStockWhenUnReturned(){
        standardBook.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(LocalDateTime.now());

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(standardBook.getAvailableCopies()).isEqualTo(6);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(result.getReturnedAt()).isNull();

        verify(bookRepository).save(standardBook);
    }

    @Test
    void loanPartialUpdate_rejectsUnReturnWhenOutOfStock(){
        standardBook.setAvailableCopies(0);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.RETURNED);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));

        assertThatThrownBy(() -> loanService.partialUpdate(10L, patch))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("No available copies");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void partialUpdate_ThrowsResourceNotFoundException_WhenLoanDoesNotExist() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.partialUpdate(99L, new LoanEntity()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Non Existing loan id:99");
    }


    @Test
    void loanDelete_returnStockForActiveLoan(){
        standardBook.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.ACTIVE);
        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));

        loanService.delete(10L);

        assertThat(standardBook.getAvailableCopies()).isEqualTo(8);
        verify(bookRepository).save(standardBook);
        verify(loanRepository).deleteById(10L);
    }

    @Test
    void loanDelete_doesNotTouchStockForReturnedLoans(){
        standardBook.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(standardBook);
        existingLoan.setStatus(LoanStatus.RETURNED); // Already returned

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));

        loanService.delete(10L);

        assertThat(standardBook.getAvailableCopies()).isEqualTo(7); // Unchanged
        verify(bookRepository, never()).save(any());
        verify(loanRepository).deleteById(10L);
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenLoanDoesNotExist() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Loan not found: 99");
    }

    @Test
    void assertNotAlreadyBorrowed_ThrowsException_IfAlreadyBorrowed() {
        when(loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(
                standardMember, standardBook, LoanStatus.RETURNED)).thenReturn(true);

        assertThatThrownBy(() -> loanService.assertNotAlreadyBorrowed(standardMember, standardBook))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("Member already has this book out");
    }

    @Test
    void markOverdueAndFine_UpdatesStatusAndCreatesFine() {
        // Arrange
        LoanEntity activeLateLoan = new LoanEntity();
        activeLateLoan.setId(1L);
        activeLateLoan.setStatus(LoanStatus.ACTIVE);
        activeLateLoan.setDueDate(LocalDateTime.now().minusDays(3));

        when(loanRepository.findByStatusAndDueDateBefore(eq(LoanStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(List.of(activeLateLoan));

        loanService.markOverdueAndFine();

        assertThat(activeLateLoan.getStatus()).isEqualTo(LoanStatus.OVERDUE);
        assertThat(activeLateLoan.getFine()).isNotNull();
        assertThat(activeLateLoan.getFine().getAmount()).isEqualTo(6); // 3 days * 2 OVERDUE_FEE
        assertThat(activeLateLoan.getFine().getIsPaid()).isFalse();

        verify(loanRepository, times(1)).save(activeLateLoan);
    }

    @Test
    void save_Success() {
        LoanEntity loan = new LoanEntity();
        when(loanRepository.save(loan)).thenReturn(loan);

        LoanEntity result = loanService.save(loan);
        assertThat(result).isNotNull();
    }

    @Test
    void isExists_ReturnsTrueFalse() {
        when(loanRepository.existsById(1L)).thenReturn(true);
        assertThat(loanService.isExists(1L)).isTrue();

        when(loanRepository.existsById(2L)).thenReturn(false);
        assertThat(loanService.isExists(2L)).isFalse();
    }

    @Test
    void findAll_ReturnsList() {
        when(loanRepository.findAll()).thenReturn(List.of(new LoanEntity()));
        assertThat(loanService.findAll()).hasSize(1);
    }

    @Test
    void findAll_PageableOnly() {
        Page<LoanEntity> page = new PageImpl<>(List.of(new LoanEntity()));
        when(loanRepository.findAll(pageable)).thenReturn(page);

        assertThat(loanService.findAll(pageable)).isNotNull();
    }

    @Test
    void findAll_WithSearch_WhenSearchIsNull_CallsStandardFindAll() {
        Page<LoanEntity> page = new PageImpl<>(List.of(new LoanEntity()));
        when(loanRepository.findAll(pageable)).thenReturn(page);

        assertThat(loanService.findAll(pageable, null)).isNotNull();
        verify(loanRepository).findAll(pageable);
    }

    @Test
    void findAll_WithSearch_WhenSearchHasText_CallsSpecification() {
        Page<LoanEntity> page = new PageImpl<>(List.of(new LoanEntity()));
        when(loanRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        assertThat(loanService.findAll(pageable, "Search Term")).isNotNull();
        verify(loanRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findOne_Success() {
        LoanEntity loan = new LoanEntity();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThat(loanService.findOne(1L)).isPresent();
    }

    @Test
    void findByMemberId_Success() {
        Page<LoanEntity> page = new PageImpl<>(List.of(new LoanEntity()));
        when(loanRepository.findByMemberEntity_Id(1L, pageable)).thenReturn(page);

        assertThat(loanService.findByMemberId(1L, pageable)).isNotNull();
    }
}