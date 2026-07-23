package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.FineDto;
import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.*;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FineMapperTest {

    private final FineMapper fineMapper = new FineMapper(new LoanMapper(new BookMapper()));

    @Test
    void mapTo_mapsFineFieldsAndNestedLoan() {
        BookEntity book = new BookEntity();
        book.setId(9L);
        book.setTitle("Dune");

        MemberEntity member = MemberEntity.builder().id(1L).memberNo(7001L).build();

        LoanEntity loan = LoanEntity.builder()
                .id(3L).memberEntity(member).bookEntity(book).status(LoanStatus.RETURNED).build();

        FineEntity fine = FineEntity.builder()
                .id(11L)
                .loanEntity(loan)
                .daysOverdue(4)
                .amount(250)
                .isPaid(false)
                .build();

        FineDto dto = fineMapper.mapTo(fine);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getDaysOverdue()).isEqualTo(4);
        assertThat(dto.getAmount()).isEqualTo(250);
        assertThat(dto.getIsPaid()).isFalse();
        assertThat(dto.getLoanDto()).isNotNull();
        assertThat(dto.getLoanDto().getId()).isEqualTo(3L);
    }

    @Test
    void mapFrom_mapsFineFieldsAndNestedLoan() {
        LoanDto loan = LoanDto.builder()
                .id(3L).memberId(1L).bookId(9L).status(LoanStatus.RETURNED).build();

        FineDto dto = FineDto.builder()
                .id(11L)
                .loanDto(loan)
                .daysOverdue(4)
                .amount(250)
                .isPaid(true)
                .build();

        FineEntity fine = fineMapper.mapFrom(dto);

        assertThat(fine.getId()).isEqualTo(11L);
        assertThat(fine.getDaysOverdue()).isEqualTo(4);
        assertThat(fine.getIsPaid()).isTrue();
        assertThat(fine.getLoanEntity()).isNotNull();
        assertThat(fine.getLoanEntity().getId()).isEqualTo(3L);
    }

    @Test
    void mapTo_withNullLoan_throwsNullPointer() {
        FineEntity fine = FineEntity.builder()
                .id(11L).loanEntity(null).daysOverdue(0).amount(0).isPaid(false)
                .build();

        assertThatThrownBy(() -> fineMapper.mapTo(fine))
                .isInstanceOf(NullPointerException.class);
    }
}