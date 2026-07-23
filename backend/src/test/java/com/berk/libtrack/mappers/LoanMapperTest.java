package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoanMapperTest {

    private final LoanMapper loanMapper = new LoanMapper(new BookMapper());


    @Test
    void mapFrom_withBothIds_createsBothReferences() {
        LoanDto dto = LoanDto.builder()
                .id(5L)
                .memberId(42L)
                .bookId(9L)
                .status(LoanStatus.ACTIVE)
                .build();

        LoanEntity entity = loanMapper.mapFrom(dto);

        assertThat(entity.getMemberEntity()).isNotNull();
        assertThat(entity.getMemberEntity().getId()).isEqualTo(42L);
        assertThat(entity.getBookEntity()).isNotNull();
        assertThat(entity.getBookEntity().getId()).isEqualTo(9L);
        assertThat(entity.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    // --- mapFrom: both branches false ---

    @Test
    void mapFrom_withNoIds_leavesBothReferencesNull() {
        LoanDto dto = LoanDto.builder()
                .id(5L)
                .memberId(null)
                .bookId(null)
                .status(LoanStatus.ACTIVE)
                .build();

        LoanEntity entity = loanMapper.mapFrom(dto);

        assertThat(entity.getMemberEntity()).isNull();
        assertThat(entity.getBookEntity()).isNull();
    }

    // --- mapFrom: mixed, so neither branch pair is only ever taken together ---

    @Test
    void mapFrom_withOnlyMemberId_leavesBookNull() {
        LoanDto dto = LoanDto.builder().id(5L).memberId(42L).bookId(null).build();

        LoanEntity entity = loanMapper.mapFrom(dto);

        assertThat(entity.getMemberEntity().getId()).isEqualTo(42L);
        assertThat(entity.getBookEntity()).isNull();
    }

    @Test
    void mapFrom_withOnlyBookId_leavesMemberNull() {
        LoanDto dto = LoanDto.builder().id(5L).memberId(null).bookId(9L).build();

        LoanEntity entity = loanMapper.mapFrom(dto);

        assertThat(entity.getMemberEntity()).isNull();
        assertThat(entity.getBookEntity().getId()).isEqualTo(9L);
    }

    // --- mapTo ---

    @Test
    void mapTo_mapsBookAndScalarFields() {
        BookEntity book = new BookEntity();
        book.setId(9L);
        book.setTitle("Dune");
        book.setIsbn(42L);

        MemberEntity member = MemberEntity.builder().id(1L).memberNo(7001L).build();

        LoanEntity entity = LoanEntity.builder()
                .id(5L)
                .memberEntity(member)
                .bookEntity(book)
                .status(LoanStatus.ACTIVE)
                .build();

        LoanDto dto = loanMapper.mapTo(entity);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getBookDto()).isNotNull();
        assertThat(dto.getBookDto().getTitle()).isEqualTo("Dune");
        assertThat(dto.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    void mapTo_writesMemberNoIntoMemberIdField() {
        MemberEntity member = MemberEntity.builder().id(1L).memberNo(7001L).build();
        BookEntity book = new BookEntity();
        book.setId(9L);

        LoanEntity entity = LoanEntity.builder()
                .id(5L).memberEntity(member).bookEntity(book).status(LoanStatus.ACTIVE).build();

        LoanDto dto = loanMapper.mapTo(entity);

        assertThat(dto.getMemberId()).isEqualTo(7001L);   // memberNo, not the id (1L)
        assertThat(dto.getMemberId()).isNotEqualTo(member.getId());
    }
}