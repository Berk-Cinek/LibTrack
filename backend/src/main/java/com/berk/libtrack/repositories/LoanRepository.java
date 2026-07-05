package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long>,
        PagingAndSortingRepository<LoanEntity, Long>,
        JpaSpecificationExecutor<LoanEntity> {

    long countByMemberEntityAndStatus(MemberEntity member, LoanStatus status);

    List<LoanEntity> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime dueDate);

    boolean existsByMemberEntityAndBookEntityAndStatusNot(MemberEntity member, BookEntity book, LoanStatus status);

}