package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.services.LoanService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    @CachePut(value = "LOAN_CACHE", key = "#result.id()" )
    public LoanEntity save(LoanEntity loanEntity) {
        return loanRepository.save(loanEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return loanRepository.existsById(id);
    }

    @Override
    @CachePut(value = "LOAN_CACHE", key = "#result.id()" )
    public LoanEntity partialUpdate(Long id, LoanEntity loanEntity) {
        loanEntity.setId(id);

        return loanRepository.findById(id).map(existingLoan ->{
            Optional.ofNullable(loanEntity.getMemberEntity()).ifPresent(existingLoan::setMemberEntity);
            Optional.ofNullable(loanEntity.getBookEntity()).ifPresent(existingLoan::setBookEntity);
            Optional.ofNullable(loanEntity.getBorrowedAt()).ifPresent(existingLoan::setBorrowedAt);
            Optional.ofNullable(loanEntity.getDueDate()).ifPresent(existingLoan::setDueDate);
            Optional.ofNullable(loanEntity.getReturnedAt()).ifPresent(existingLoan::setReturnedAt);
            Optional.ofNullable(loanEntity.getStatus()).ifPresent(existingLoan::setStatus);
            return loanRepository.save(existingLoan);
        }).orElseThrow(() -> new RuntimeException("Non Existing loan"));
    }

    @Override
    @CacheEvict(value = "LOAN_CACHE", key = "#id")
    public void delete(Long id) {
        loanRepository.deleteById(id);
    }

    @Override
    public List<LoanEntity> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Page<LoanEntity> findAll(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "LOAN_CACHE", key = "#id")
    public Optional<LoanEntity> findOne(Long id) {
        return loanRepository.findById(id);
    }
}
