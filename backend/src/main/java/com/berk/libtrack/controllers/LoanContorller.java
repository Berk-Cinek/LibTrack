package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.mappers.LoanMapper;
import com.berk.libtrack.security.services.AuthService;
import com.berk.libtrack.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Loans", description = "Basic CRUD functionality for Fines + pagination for return-all")
@RestController
public class LoanContorller {

    private LoanService loanService;
    private LoanMapper loanMapper;
    private AuthService authService;

    public LoanContorller(LoanService loanService, LoanMapper loanMapper, AuthService authService) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
        this.authService = authService;
    }

    @Operation(summary = "Create a Loans", description = "Adds a new book to the catalog." +
            " Fields: id, member(FK), book(FK), borrowedAt, dueDate, returnedAt, status.")
    @PostMapping(path = "/loans")
    public ResponseEntity<LoanDto> createLoan(@RequestBody LoanDto loanDto){
        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity savedEntity = loanService.loanCreate(loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all loan of member", description = "get all loans of a loged-in member")
    @GetMapping("/loans/mine")
    public ResponseEntity<Page<LoanDto>> getMyLoans(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Long memberId = authService.getMemberIdForUsername(userDetails.getUsername());

        return ResponseEntity.ok(loanService.findByMemberId(memberId, pageable)
                .map(loanMapper::mapTo));
    }

    @Operation(summary = "Get all Loan", description = "Get all Loan with pagination + search string")
    @GetMapping(path = "/loans")
    public Page<LoanDto> listLoans(Pageable pageable, @RequestParam(required = false) String search){
        Page<LoanEntity> loans = loanService.findAll(pageable, search);
        return loans.map(loanMapper::mapTo);
    }

    @Operation(summary = "Get one Loan", description = "Get one Loan based on id match")
    @GetMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> getById(@PathVariable("id") Long id) {

        Optional<LoanEntity> foundLoan = loanService.findOne(id);
        return foundLoan.map(loanEntity -> {
            LoanDto loanDto = loanMapper.mapTo(loanEntity);
            return new ResponseEntity<>(loanDto, HttpStatus.OK);
        }).orElse( new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Fully update Loan", description = "Fully update one Loan based on id match")
    @PutMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> fullUpdate(@PathVariable("id") Long id, @RequestBody LoanDto loanDto){
        if (!loanService.isExists(id)){
            throw new ResourceNotFoundException("Loan with id:" + id + "not found for full update");
        }

        loanDto.setId(id);
        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity savedLoanEntity = loanService.save(loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(savedLoanEntity), HttpStatus.OK);
    }

    @Operation(summary = "Partial update Loan", description = "Partially update one Loan based on id match, " +
            "any given value will change those which are not given stay the same")
    @PatchMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> partialUpdate(@PathVariable("id") Long id, @RequestBody LoanDto loanDto){
        if (!loanService.isExists(id)){
            throw new ResourceNotFoundException("Loan with id:" + id + "not found for partial update");
        }

        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity updatedLoan = loanService.partialUpdate(id, loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(updatedLoan), HttpStatus.OK);
    }

    @Operation(summary = "Delete Loan", description = "Delete Loan based on id match")
    @DeleteMapping(path = "loans/{id}")
    public ResponseEntity deleteLoan(@PathVariable("id") Long id){
        if (!loanService.isExists(id)) {
            throw new ResourceNotFoundException("Loan with id:" + id + "not found for deletion");
        }

        loanService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
