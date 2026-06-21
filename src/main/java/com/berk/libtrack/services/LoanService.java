package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.LoanEntity;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    LoanEntity save(LoanEntity loanEntity);

    Boolean isExists(Long id);

    LoanEntity partialUpdate(Long id, LoanEntity loanEntity);

    void delete(Long id);

    List<LoanEntity> findAll();

    Optional<LoanEntity> findOne(Long id);
}
