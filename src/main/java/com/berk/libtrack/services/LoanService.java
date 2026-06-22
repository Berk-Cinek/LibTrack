package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    LoanEntity save(LoanEntity loanEntity);

    Boolean isExists(Long id);

    LoanEntity partialUpdate(Long id, LoanEntity loanEntity);

    void delete(Long id);

    List<LoanEntity> findAll();

    Page<LoanEntity> findAll(Pageable pageable);

    Optional<LoanEntity> findOne(Long id);
}
