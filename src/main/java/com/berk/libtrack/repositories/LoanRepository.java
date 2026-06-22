package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long>, PagingAndSortingRepository<LoanEntity, Long> {
}
