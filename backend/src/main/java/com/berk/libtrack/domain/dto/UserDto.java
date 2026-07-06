package com.berk.libtrack.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID of the member this login belongs to", example = "5")
    private Long memberId;

    @Schema(description = "Login username", example = "bcinek")
    private String username;

    @Schema(description = "Role determining access level", example = "ADMIN")
    private String role;
}