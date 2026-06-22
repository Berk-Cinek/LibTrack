package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.*;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.services.LoanService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Integer LOAN_PERIOD_DAYS = 14;
    private static final Integer OVERDUE_FEE = 2;

    private  BookRepository bookRepository;
    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @CachePut(value = "LOAN_CACHE", key = "#result.id()" )
    public LoanEntity save(LoanEntity loanEntity) {
        return loanRepository.save(loanEntity);
    }

    @Override
    @Transactional
    public LoanEntity loanCreate(LoanEntity loanEntity) {

        MemberEntity memberEntity = loanEntity.getMemberEntity();
        BookEntity bookEntity = bookRepository.findById(loanEntity.getBookEntity().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));


        assertCanBorrow(memberEntity);
        assertInStock(bookEntity);

        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() - 1);

        loanEntity.setBookEntity(bookEntity);
        loanEntity.setBorrowedAt(LocalDateTime.now());
        loanEntity.setDueDate(LocalDateTime.now().plusDays(LOAN_PERIOD_DAYS));
        loanEntity.setStatus(LoanStatus.ACTIVE);

        return loanRepository.save(loanEntity);
    }

    @Override
    public void assertCanBorrow(MemberEntity memberEntity) {
        Long countActive = loanRepository.countByMemberEntityAndStatus(memberEntity, LoanStatus.ACTIVE);
        Long countOverDue = loanRepository.countByMemberEntityAndStatus(memberEntity, LoanStatus.OVERDUE);
        if (countOverDue >= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Members with overdue books cannot borrow new ones");
        }else if (countActive >= 3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Member already has 3 active loans");
        }
    }

    @Override
    public void assertInStock(BookEntity bookEntity) {
        if (bookEntity.getAvailableCopies() <= 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No available copies");
    }

    @Override
    public void assertNotAlreadyBorrowed(MemberEntity memberEntity, BookEntity bookEntity) {
        if (loanRepository.existsByMemberEntityAndBookEntityAndStatusNot(memberEntity, bookEntity, LoanStatus.RETURNED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Member already has this book out");

        }
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

    @Scheduled(cron = "0 0 0 1 * *")
    public void markOverdueAndFine(){
        var due = loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDateTime.now());

        for(LoanEntity loan :  due){
            loan.setStatus(LoanStatus.OVERDUE);
            Integer daysLate = Math.toIntExact(ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now()));

            FineEntity fine = FineEntity.builder()
                    .loanEntity(loan)
                    .amount(OVERDUE_FEE * daysLate)
                    .isPaid(false)
                    .build();

            loan.setFine(fine);
            loanRepository.save(loan);
        }
    }
}
