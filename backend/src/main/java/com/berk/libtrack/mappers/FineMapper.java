package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.FineDto;
import com.berk.libtrack.domain.entities.FineEntity;
import org.springframework.stereotype.Component;

@Component
public class FineMapper implements Mapper<FineEntity, FineDto> {

    private LoanMapper loanMapper;

    public FineMapper(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    @Override
    public FineDto mapTo(FineEntity fineEntity) {
        return FineDto.builder()
                .id(fineEntity.getId())
                .loanDto(loanMapper.mapTo(fineEntity.getLoanEntity()))
                .daysOverdue(fineEntity.getDaysOverdue())
                .amount(fineEntity.getAmount())
                .isPaid(fineEntity.getIsPaid())
                .paidAt(fineEntity.getPaidAt())
                .build();
    }

    @Override
    public FineEntity mapFrom(FineDto fineDto) {
        return FineEntity.builder()
                .id(fineDto.getId())
                .loanEntity(loanMapper.mapFrom(fineDto.getLoanDto()))
                .daysOverdue(fineDto.getDaysOverdue())
                .amount(fineDto.getAmount())
                .isPaid(fineDto.getIsPaid())
                .paidAt(fineDto.getPaidAt())
                .build();
    }
}
