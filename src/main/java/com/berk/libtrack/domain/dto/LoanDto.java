package com.berk.libtrack.domain.dto;

import com.berk.libtrack.domain.entities.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {

    private Long id;

    private Long memberId;

    private BookDto bookDto;

    private LocalDateTime borrowedAt;

    private LocalDateTime dueDate;

    private LocalDateTime returnedAt;

    private LoanStatus status;
}
