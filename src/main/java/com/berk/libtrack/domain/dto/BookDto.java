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
public class BookDto {

    @Schema(description = "Unique identifier", example = "8", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ISBN", example = "9780140449266")
    private Long isbn;

    @Schema(description = "title of book", example = "The Myth of Sisyphus")
    private String title;

    @Schema(description = "author of book", example = "Albert Camus")
    private String author;

    @Schema(description = "genre of book", example = "philosophy")
    private String genre;

    @Schema(description = "total copies including ones out on loan", example = "30")
    private Integer totalCopies;

    @Schema(description = "Copies available to borrow", example = "20")
    private Integer availableCopies;

    @Schema(description = "When the Book record was created (set by the server)",
            example = "2026-06-22T17:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
