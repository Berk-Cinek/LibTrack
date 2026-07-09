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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void loanCreate_rejectsMemberWithThreeActiveLoans(){
        MemberEntity member = new MemberEntity();
        member.setId(1L);

        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(10);


        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.ACTIVE)).thenReturn(3L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("3 active loans");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_acceptMemberWithLessThenThreeLoans(){
        MemberEntity member = new MemberEntity();
        member.setId(1L);

        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(10);


        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.ACTIVE)).thenReturn(2L);

        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.loanCreate(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(result.getBorrowedAt()).isNotNull();
        assertThat(book.getAvailableCopies()).isEqualTo(9);

        verify(loanRepository).save(any());
    }

    @Test
    void loanCreate_rejectMemberWithOverdueBook(){
        MemberEntity member = new MemberEntity();
        member.setId(1L);

        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(10);


        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.OVERDUE)).thenReturn(1L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("overdue books cannot borrow new ones");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_rejectWhenBookOutOfStock(){
        MemberEntity member = new MemberEntity();
        member.setId(1L);

        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(0);


        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.ACTIVE)).thenReturn(0L);

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("No available copies");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanCreate_succeedsWithLastCopy(){
        MemberEntity member = new MemberEntity();
        member.setId(1L);

        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(1);


        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.OVERDUE)).thenReturn(0L);
        when(loanRepository.countByMemberEntityAndStatus(member, LoanStatus.ACTIVE)).thenReturn(0L);

        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.loanCreate(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(result.getBorrowedAt()).isNotNull();
        assertThat(book.getAvailableCopies()).isEqualTo(0);

        verify(loanRepository).save(any());
    }

    @Test
    void loanCreate_rejectUnknownMember(){
        MemberEntity member = new MemberEntity();
        member.setId(99L);

        BookEntity book = new BookEntity();
        book.setId(5L);

        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanPartialUpdate_returnStockWhenMarkedReturned(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.ACTIVE);
        existingLoan.setReturnedAt(null);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(book.getAvailableCopies()).isEqualTo(8);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();

        verify(bookRepository).save(book);
    }

    @Test
    void loanPartialUpdate_doesNotDoubleIncrementWhenAlreadyReturned(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(null);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(book.getAvailableCopies()).isEqualTo(7);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();

        verify(bookRepository).save(book);
    }

    @Test
    void loanPartialUpdate_decrementStockWhenUnReturned(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(null);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoanEntity result = loanService.partialUpdate(10L, patch);

        assertThat(book.getAvailableCopies()).isEqualTo(6);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();

        verify(bookRepository).save(book);
    }

    @Test
    void loanPartialUpdate_rejectsUnReturnWhenOutOfStock(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(0);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(null);

        LoanEntity patch = new LoanEntity();
        patch.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> loanService.partialUpdate(10L, patch))
                .isInstanceOf(BorrowingNotAllowedException.class)
                .hasMessageContaining("No available copies");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanDelete_returnStockForReturnedLoan(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.ACTIVE);
        existingLoan.setReturnedAt(null);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));

        loanService.delete(10L);

        assertThat(book.getAvailableCopies()).isEqualTo(8);
        verify(bookRepository).save(book);
        verify(loanRepository).deleteById(10L);
    }

    @Test
    void loanDelete_doesNotTouchStockForReturnedLoans(){
        BookEntity book = new BookEntity();
        book.setId(5L);
        book.setAvailableCopies(7);

        LoanEntity existingLoan = new LoanEntity();
        existingLoan.setId(10L);
        existingLoan.setBookEntity(book);
        existingLoan.setStatus(LoanStatus.RETURNED);
        existingLoan.setReturnedAt(null);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(existingLoan));

        loanService.delete(10L);

        assertThat(book.getAvailableCopies()).isEqualTo(7);
        verify(loanRepository).deleteById(10L);
    }
}