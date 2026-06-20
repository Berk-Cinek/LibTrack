package com.berk.libtrack.domain.dto;

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

    private Long id;

    private Long memberNo;

    private String fullName;

    private String email;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private List<LoanDto> loans =new ArrayList<>();
}
