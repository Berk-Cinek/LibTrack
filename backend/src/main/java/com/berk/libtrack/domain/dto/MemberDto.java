package com.berk.libtrack.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    @Schema(description = "Unique identifier", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "uniqe number of the member", example = "73")
    private Long memberNo;

    @Schema(description = "full name of the member", example = "John Smith")
    private String fullName;

    @Schema(description = "email of the member", example = "Smith@gmail.com")
    private String email;

    @Schema(description = "boolean check for identifying member status", example = "true")
    private Boolean isActive;

    @Schema(description = "When the Member record was created (set by the server)",
            example = "2026-06-22T17:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Loans belonging to this member", accessMode = Schema.AccessMode.READ_ONLY)
    private List<LoanDto> loans =new ArrayList<>();
}
