package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.domain.entities.MemberEntity;
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
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private MemberEntity alice;
    private MemberEntity bob;
    private BookEntity myth;
    private BookEntity picnic;

    @BeforeEach
    void seedWorld() {
        alice = memberRepository.save(member(4001L, "Alice Test", "alice@x.com"));
        bob = memberRepository.save(member(4002L, "Bob Test", "bob@x.com"));
        myth = bookRepository.save(book("The Myth of Sisyphus"));
        picnic = bookRepository.save(book("Roadside Picnic"));
    }

    @Test
    void countByMemberEntityAndStatus_countsOnlyThatMembersLoansInThatStatus() {
        loanRepository.save(loan(alice, myth, LoanStatus.ACTIVE, daysFromNow(10)));
        loanRepository.save(loan(alice, picnic, LoanStatus.ACTIVE, daysFromNow(10)));
        loanRepository.save(loan(alice, myth, LoanStatus.RETURNED, daysFromNow(-5)));
        loanRepository.save(loan(bob, myth, LoanStatus.ACTIVE, daysFromNow(10)));   // other member

        assertThat(loanRepository.countByMemberEntityAndStatus(alice, LoanStatus.ACTIVE)).isEqualTo(2);
        assertThat(loanRepository.countByMemberEntityAndStatus(alice, LoanStatus.RETURNED)).isEqualTo(1);
        assertThat(loanRepository.countByMemberEntityAndStatus(alice, LoanStatus.OVERDUE)).isZero();
    }


    @Test
    void findByStatusAndDueDateBefore_findsOnlyActivePastDueLoans() {
        LoanEntity overdueCandidate = loanRepository.save(loan(alice, myth, LoanStatus.ACTIVE, daysFromNow(-3)));
        loanRepository.save(loan(alice, picnic, LoanStatus.ACTIVE, daysFromNow(+3)));
        loanRepository.save(loan(bob, picnic, LoanStatus.RETURNED, daysFromNow(-10)));

        List<LoanEntity> result =
                loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDateTime.now());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(overdueCandidate.getId());
    }


    @Test
    void existsByMemberEntityAndBookEntityAndStatusNot_detectsOpenLoanOfSameBook() {
        loanRepository.save(loan(alice, myth, LoanStatus.ACTIVE, daysFromNow(10)));

        assertThat(loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(
                alice, myth, LoanStatus.RETURNED)).isTrue();

        assertThat(loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(
                bob, myth, LoanStatus.RETURNED)).isFalse();

        assertThat(loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(
                alice, picnic, LoanStatus.RETURNED)).isFalse();
    }

    @Test
    void existsByMemberEntityAndBookEntityAndStatusNot_falseOnceLoanReturned() {
        loanRepository.save(loan(alice, myth, LoanStatus.RETURNED, daysFromNow(-5)));

        assertThat(loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(
                alice, myth, LoanStatus.RETURNED)).isFalse();
    }

    @Test
    void findByMemberEntityId_returnsOnlyThatMembersLoans() {
        loanRepository.save(loan(alice, myth, LoanStatus.ACTIVE, daysFromNow(10)));
        loanRepository.save(loan(alice, picnic, LoanStatus.RETURNED, daysFromNow(-5)));
        loanRepository.save(loan(bob, myth, LoanStatus.ACTIVE, daysFromNow(10)));

        Page<LoanEntity> alicesLoans =
                loanRepository.findByMemberEntity_Id(alice.getId(), PageRequest.of(0, 10));

        assertThat(alicesLoans.getContent()).hasSize(2);
        assertThat(alicesLoans.getContent())
                .allMatch(l -> l.getMemberEntity().getId().equals(alice.getId()));
    }


    @Test
    void existsByMemberEntityId_and_existsByBookEntityId_bothDirections() {
        loanRepository.save(loan(alice, myth, LoanStatus.ACTIVE, daysFromNow(10)));

        assertThat(loanRepository.existsByMemberEntity_Id(alice.getId())).isTrue();
        assertThat(loanRepository.existsByMemberEntity_Id(bob.getId())).isFalse();

        assertThat(loanRepository.existsByBookEntity_Id(myth.getId())).isTrue();
        assertThat(loanRepository.existsByBookEntity_Id(picnic.getId())).isFalse();
    }


    private LocalDateTime daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days);
    }

    private MemberEntity member(Long memberNo, String fullName, String email) {
        return MemberEntity.builder()
                .memberNo(memberNo)
                .fullName(fullName)
                .email(email)
                .isActive(true)
                .build();
    }

    private BookEntity book(String title) {
        BookEntity b = new BookEntity();
        b.setIsbn(Math.abs((long) title.hashCode()));
        b.setTitle(title);
        b.setAuthor("Test Author");
        b.setGenre("test");
        b.setTotalCopies(5);
        b.setAvailableCopies(5);
        return b;
    }

    private LoanEntity loan(MemberEntity m, BookEntity b, LoanStatus status, LocalDateTime dueDate) {
        LoanEntity l = new LoanEntity();
        l.setMemberEntity(m);
        l.setBookEntity(b);
        l.setBorrowedAt(LocalDateTime.now().minusDays(14));
        l.setDueDate(dueDate);
        l.setStatus(status);
        if (status == LoanStatus.RETURNED) {
            l.setReturnedAt(LocalDateTime.now().minusDays(1));
        }
        return l;
    }
}