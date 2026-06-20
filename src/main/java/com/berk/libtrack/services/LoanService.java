package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.LoanEntity;

public interface LoanService {

    LoanEntity save(LoanEntity loanEntity);

    Boolean isExists(Long id);

    LoanEntity partialUpdate(Long id, LoanEntity loanEntity);

    void delete(Long id);
}
