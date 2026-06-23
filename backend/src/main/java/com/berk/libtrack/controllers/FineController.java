package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.FineDto;
import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.mappers.FineMapper;
import com.berk.libtrack.services.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Fines", description = "Basic CRUD functionality for Fines + pagination for return-all")
@RestController
public class FineController {

    private FineMapper fineMapper;
    private FineService fineService;

    public FineController(FineMapper fineMapper, FineService fineService) {
        this.fineMapper = fineMapper;
        this.fineService = fineService;
    }

    @Operation(summary = "Create a Book", description = "Adds a new fine to the catalog." +
            "Fields: id, loan(FK), daysOverDue, amount, isPaid, PaidAt.")
    @PostMapping(path = "/fines")
    public ResponseEntity<FineDto> createFine(@RequestBody FineDto fineDto){
        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity savedEntity = fineService.save(fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Fines", description = "Get all Fines with pagination")
    @GetMapping(path = "/fines")
    public Page<FineDto> listFines(Pageable pageable){
        Page<FineEntity> fines = fineService.findAll(pageable);
        return fines.map(fineMapper::mapTo);
    }

    @Operation(summary = "Get one Fine", description = "Get one Fine based on id match")
    @GetMapping(path = "/fines/{id}")
    public ResponseEntity<FineDto> getById(@PathVariable("id") Long id){

        Optional<FineEntity> foundFine = fineService.findOne(id);
        return foundFine.map(fineEntity -> {
            FineDto fineDto = fineMapper.mapTo(fineEntity);
            return new ResponseEntity(fineDto, HttpStatus.OK);
        }).orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Fully update Fine", description = "Fully update one Fine based on id match")
    @PutMapping(path = "/fines/{id}")
    public ResponseEntity<FineDto> fullUpdate(@PathVariable("id") Long id, @RequestBody FineDto fineDto){
        if (!fineService.isExists(id)){
            throw new ResourceNotFoundException("Fine with id:" + id + "not found for full update");
        }

        fineDto.setId(id);
        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity savedFineEntity = fineService.save(fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(savedFineEntity), HttpStatus.OK);
    }

    @Operation(summary = "Partial update Fine", description = "Partially update one Fine based on id match, " +
            "any given value will change those which are not given will stay the same")
    @PatchMapping(path = "fines/{id}")
    public ResponseEntity<FineDto> partialUpdate(@PathVariable("id") Long id, @RequestBody FineDto fineDto){
        if (!fineService.isExists(id)){
            throw new ResourceNotFoundException("Fine with id:" + id + "not found for partial update");
        }

        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity updatedFine = fineService.partialUpdate(id, fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(updatedFine), HttpStatus.OK);
    }
    @Operation(summary = "Delete Book", description = "Delete Book based on id match")
    @DeleteMapping(path = "fines/{id}")
    public ResponseEntity deleteFine(@PathVariable("id") Long id){
        if (!fineService.isExists(id)) {
            throw new ResourceNotFoundException("Fine with id:" + id + "not found for deletion");
        }

        fineService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
