package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.mappers.LoanMapper;
import com.berk.libtrack.services.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoanContorller {

    private LoanService loanService;
    private LoanMapper loanMapper;

    public LoanContorller(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    @PostMapping(path = "/loans")
    public ResponseEntity<LoanDto> createLoan(@RequestBody LoanDto loanDto){
        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity savedEntity = loanService.save(loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/loans")
    public List<LoanDto> listAuthors(){
        List<LoanEntity> books = loanService.findAll();
        return books.stream()
                .map(loanMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> getById(@PathVariable("id") Long id) {
        Optional<LoanEntity> foundLoan = loanService.findOne(id);
        return foundLoan.map(loanEntity -> {
            LoanDto loanDto = loanMapper.mapTo(loanEntity);
            return new ResponseEntity<>(loanDto, HttpStatus.OK);
        }).orElse( new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> fullUpdate(@PathVariable("id") Long id, @RequestBody LoanDto loanDto){
        if (!loanService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        loanDto.setId(id);
        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity savedLoanEntity = loanService.save(loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(savedLoanEntity), HttpStatus.OK);
    }

    @PatchMapping(path = "/loans/{id}")
    public ResponseEntity<LoanDto> partialUpdate(@PathVariable("id") Long id, @RequestBody LoanDto loanDto){
        if (!loanService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LoanEntity loanEntity = loanMapper.mapFrom(loanDto);
        LoanEntity updatedLoan = loanService.partialUpdate(id, loanEntity);
        return new ResponseEntity<>(loanMapper.mapTo(updatedLoan), HttpStatus.OK);
    }

    @DeleteMapping(path = "loans/{id}")
    public ResponseEntity deleteLoan(@PathVariable("id") Long id){
        if (!loanService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        loanService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
