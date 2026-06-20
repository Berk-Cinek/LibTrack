package com.berk.libtrack.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDto {

    private Long id;

    private LoanDto loanDto;

    private Integer daysOverdue;

    private Integer amount;

    private Boolean isPaid;

    private LocalDateTime paidAt;
}
