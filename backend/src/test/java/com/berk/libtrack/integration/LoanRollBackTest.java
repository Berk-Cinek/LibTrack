package com.berk.libtrack.integration;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.services.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Import(TestcontainersConfig.class)
class LoanRollbackIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private LoanRepository loanRepository;

    @Test
    void loanCreate_rollsBackStockDecrement_whenSaveFailsMidway() {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberNo(8001L).fullName("Rollback Person").email("rb@x.com").isActive(true).build());

        BookEntity book = new BookEntity();
        book.setIsbn(999888777L);
        book.setTitle("Rollback Book");
        book.setAuthor("Test");
        book.setGenre("test");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        book = bookRepository.save(book);

        doThrow(new RuntimeException("simulated crash at save"))
                .when(loanRepository).save(any(LoanEntity.class));

        LoanEntity request = new LoanEntity();
        request.setMemberEntity(member);
        request.setBookEntity(book);

        //failure
        assertThatThrownBy(() -> loanService.loanCreate(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("simulated crash");

        //check
        BookEntity reloaded = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(reloaded.getAvailableCopies()).isEqualTo(5);
    }
}