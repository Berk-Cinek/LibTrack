package com.berk.libtrack.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "members")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long memberNo;

    @Column(nullable = false, unique = true)
    private String fullName;

    private String email;
    private Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    private List<LoanEntity> loanEntities =new ArrayList<>();

    @OneToOne(mappedBy = "memberEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserEntity userEntity;
}
