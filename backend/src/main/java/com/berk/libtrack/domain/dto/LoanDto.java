package com.berk.libtrack.domain.dto;

import com.berk.libtrack.domain.entities.LoanStatus;
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
public class LoanDto {

    @Schema(description = "Unique identifier", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Id number that will be compared against members that this loan belongs to", example = "2")
    private Long memberId;

    @Schema(description = "The book that is taken out on loan")
    private BookDto bookDto;

    @Schema(description = "When the loan record was created (set by the server)",
            example = "2026-06-22T18:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime borrowedAt;

    @Schema(description = "When the loan will create a fine (set by the server, default of 14 days)",
            example = "2026-07-22T01:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dueDate;

    @Schema(description = "When the Book is returned",
            example = "2026-07-22T01:30:00")
    private LocalDateTime returnedAt;

    @Schema(description = "status enum traking loan status", example = "ACTIVE")
    private LoanStatus status;
}
