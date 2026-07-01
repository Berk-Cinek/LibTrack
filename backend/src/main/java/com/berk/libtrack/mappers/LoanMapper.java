package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper implements Mapper<LoanEntity, LoanDto> {

    private final BookMapper bookMapper;

    public LoanMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }


    @Override
    public LoanDto mapTo(LoanEntity loanEntity) {
        return LoanDto.builder()
                .id(loanEntity.getId())
                .memberId(loanEntity.getMemberEntity().getId())
                .bookDto(bookMapper.mapTo(loanEntity.getBookEntity()))
                .borrowedAt(loanEntity.getBorrowedAt())
                .dueDate(loanEntity.getDueDate())
                .returnedAt(loanEntity.getReturnedAt())
                .status(loanEntity.getStatus())
                .build();
    }

    @Override
    public LoanEntity mapFrom(LoanDto loanDto) {
        // there must be a better way of doing this
        MemberEntity memberRef = null;
        if (loanDto.getMemberId() != null) {
            memberRef = new MemberEntity();
            memberRef.setId(loanDto.getMemberId());
        }

        BookEntity bookRef = null;
        if (loanDto.getBookId() != null) {
            bookRef = new BookEntity();
            bookRef.setId(loanDto.getBookId());
        }

        return LoanEntity.builder()
                .id(loanDto.getId())
                .memberEntity(memberRef)
                .bookEntity(bookRef)
                .borrowedAt(loanDto.getBorrowedAt())
                .dueDate(loanDto.getDueDate())
                .returnedAt(loanDto.getReturnedAt())
                .status(loanDto.getStatus())
                .build();
    }
}
