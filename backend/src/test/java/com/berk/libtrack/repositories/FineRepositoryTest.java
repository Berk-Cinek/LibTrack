package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class FineRepositoryTest {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private LoanEntity overdueLoanWithFine;

    @BeforeEach
    void seedChain() {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberNo(3001L).fullName("Chain Person").email("chain@x.com").isActive(true)
                .build());

        BookEntity mythBook = bookRepository.save(book("The Myth of Sisyphus"));
        BookEntity otherBook = bookRepository.save(book("Roadside Picnic"));

        overdueLoanWithFine = loanRepository.save(loan(member, mythBook, LoanStatus.OVERDUE));
        LoanEntity activeCleanLoan = loanRepository.save(loan(member, otherBook, LoanStatus.ACTIVE));

        FineEntity fine = new FineEntity();
        fine.setLoanEntity(overdueLoanWithFine);
        fine.setDaysOverdue(5);
        fine.setAmount(12);
        fine.setIsPaid(false);
        fineRepository.save(fine);
    }


    @Test
    void search_reachesThroughLoanToBookTitle() {
        Page<FineEntity> hit = fineRepository
                .findByLoanEntity_BookEntity_TitleContainingIgnoreCase("myth", PageRequest.of(0, 10));

        assertThat(hit.getContent()).hasSize(1);
        assertThat(hit.getContent().getFirst().getLoanEntity().getBookEntity().getTitle())
                .isEqualTo("The Myth of Sisyphus");
    }

    @Test
    void search_findsNothingForBookWhoseLoanHasNoFine() {
        Page<FineEntity> miss = fineRepository
                .findByLoanEntity_BookEntity_TitleContainingIgnoreCase("picnic", PageRequest.of(0, 10));

        assertThat(miss.getContent()).isEmpty();
    }


    @Test
    void findByLoanEntityStatus_returnsFinesWhoseLoanHasThatStatus() {
        List<FineEntity> overdueFines = fineRepository.findByLoanEntityStatus(LoanStatus.OVERDUE);
        List<FineEntity> activeFines = fineRepository.findByLoanEntityStatus(LoanStatus.ACTIVE);

        assertThat(overdueFines).hasSize(1);
        assertThat(overdueFines.getFirst().getLoanEntity().getId())
                .isEqualTo(overdueLoanWithFine.getId());

        assertThat(activeFines).isEmpty();
    }


    private BookEntity book(String title) {
        BookEntity book = new BookEntity();
        book.setIsbn(Math.abs((long) title.hashCode()));
        book.setTitle(title);
        book.setAuthor("Test Author");
        book.setGenre("test");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        return book;
    }

    private LoanEntity loan(MemberEntity memberEntity, BookEntity bookEntity, LoanStatus status) {
        LoanEntity loan = new LoanEntity();
        loan.setMemberEntity(memberEntity);
        loan.setBookEntity(bookEntity);
        loan.setBorrowedAt(LocalDateTime.now().minusDays(20));
        loan.setDueDate(status == LoanStatus.OVERDUE
                ? LocalDateTime.now().minusDays(6)
                : LocalDateTime.now().plusDays(8));
        loan.setStatus(status);
        return loan;
    }
}