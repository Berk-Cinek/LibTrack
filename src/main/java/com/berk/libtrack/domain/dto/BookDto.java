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
public class BookDto {

    private Long id;

    private Long isbn;

    private String title;

    private String author;

    private String genre;

    private Integer totalCopies;

    private Integer availableCopies;

    private LocalDateTime createdAt;
}
