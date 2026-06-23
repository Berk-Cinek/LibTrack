package com.berk.libtrack.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "The loan this fine belongs to" )
    private LoanDto loanDto;

    @Schema(description = "amount of days overdue to return",
            example = "4"
            ,accessMode = Schema.AccessMode.READ_ONLY)
    private Integer daysOverdue;

    @Schema(description = "amount of money to be charged as a fine",
            example = "4"
            ,accessMode = Schema.AccessMode.READ_ONLY)
    private Integer amount;

    @Schema(description = "Boolean check for payment")
    private Boolean isPaid;

    @Schema(description = "When the fine record was paid (set by the server)",
            example = "2026-07-22T17:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime paidAt;
}
