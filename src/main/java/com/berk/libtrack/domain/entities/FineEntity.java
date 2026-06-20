package com.berk.libtrack.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "Fines")
public class FineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "loan_id")
    private LoanEntity loanEntity;

    private Integer daysOverdue;

    private Integer amount;

    private Boolean isPaid;

    private LocalDateTime paidAt;
}
