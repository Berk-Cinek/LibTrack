package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.dto.MemberDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperTest {

    private final MemberMapper memberMapper = new MemberMapper(new LoanMapper(new BookMapper()));


    @Test
    void mapTo_withLoans_mapsEachLoan() {
        BookEntity book = new BookEntity();
        book.setId(9L);
        book.setTitle("Dune");

        MemberEntity member = MemberEntity.builder()
                .id(1L).memberNo(7001L).fullName("Bob").email("bob@x.com").isActive(true)
                .build();

        LoanEntity loan = LoanEntity.builder()
                .id(3L).memberEntity(member).bookEntity(book).status(LoanStatus.ACTIVE).build();
        member.setLoanEntities(List.of(loan));

        MemberDto dto = memberMapper.mapTo(member);

        assertThat(dto.getFullName()).isEqualTo("Bob");
        assertThat(dto.getLoans()).hasSize(1);
        assertThat(dto.getLoans().getFirst().getId()).isEqualTo(3L);
    }

    @Test
    void mapTo_withNullLoans_yieldsEmptyListNotNull() {
        MemberEntity member = MemberEntity.builder()
                .id(1L).memberNo(7001L).fullName("Bob").loanEntities(null)
                .build();

        MemberDto dto = memberMapper.mapTo(member);

        assertThat(dto.getLoans()).isNotNull();
        assertThat(dto.getLoans()).isEmpty();
    }

    // --- mapFrom ---

    @Test
    void mapFrom_withLoans_mapsEachLoan() {
        LoanDto loan = LoanDto.builder()
                .id(3L).memberId(1L).bookId(9L).status(LoanStatus.ACTIVE).build();

        MemberDto dto = MemberDto.builder()
                .id(1L).memberNo(7001L).fullName("Bob").email("bob@x.com").isActive(true)
                .loans(List.of(loan))
                .build();

        MemberEntity member = memberMapper.mapFrom(dto);

        assertThat(member.getFullName()).isEqualTo("Bob");
        assertThat(member.getLoanEntities()).hasSize(1);
        assertThat(member.getLoanEntities().getFirst().getId()).isEqualTo(3L);
    }

    @Test
    void mapFrom_withNullLoans_yieldsEmptyListNotNull() {
        MemberDto dto = MemberDto.builder()
                .id(1L).memberNo(7001L).fullName("Bob").loans(null)
                .build();

        MemberEntity member = memberMapper.mapFrom(dto);

        assertThat(member.getLoanEntities()).isNotNull();
        assertThat(member.getLoanEntities()).isEmpty();
    }

    @Test
    void mapFrom_withEmptyLoanList_yieldsEmptyList() {
        MemberDto dto = MemberDto.builder()
                .id(1L).memberNo(7001L).fullName("Bob").loans(List.of())
                .build();

        assertThat(memberMapper.mapFrom(dto).getLoanEntities()).isEmpty();
    }
}