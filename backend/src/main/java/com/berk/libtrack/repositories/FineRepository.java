package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.domain.entities.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<FineEntity, Long>, PagingAndSortingRepository<FineEntity, Long> {

   List<FineEntity> findByLoanEntityStatus(LoanStatus status);

   Page<FineEntity> findByLoanEntity_BookEntity_TitleContainingIgnoreCase(String title, Pageable pageable);
}