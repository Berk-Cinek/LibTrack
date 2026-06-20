package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.FineDto;
import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.mappers.FineMapper;
import com.berk.libtrack.services.FineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FineController {

    private FineMapper fineMapper;
    private FineService fineService;

    public FineController(FineMapper fineMapper, FineService fineService) {
        this.fineMapper = fineMapper;
        this.fineService = fineService;
    }

    @PostMapping(path = "/fines")
    public ResponseEntity<FineDto> createFine(@RequestBody FineDto fineDto){
        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity savedEntity = fineService.save(fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }

    @PutMapping(path = "/fines/{id}")
    public ResponseEntity<FineDto> fullUpdate(@PathVariable("id") Long id, @RequestBody FineDto fineDto){
        if (!fineService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fineDto.setId(id);
        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity savedFineEntity = fineService.save(fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(savedFineEntity), HttpStatus.OK);
    }

    @PatchMapping(path = "fines/{id}")
    public ResponseEntity<FineDto> partialUpdate(@PathVariable("id") Long id, @RequestBody FineDto fineDto){
        if (!fineService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FineEntity fineEntity = fineMapper.mapFrom(fineDto);
        FineEntity updatedFine = fineService.partialUpdate(id, fineEntity);
        return new ResponseEntity<>(fineMapper.mapTo(updatedFine), HttpStatus.OK);
    }

    @DeleteMapping(path = "fines/{id}")
    public ResponseEntity deleteFine(@PathVariable("id") Long id){
        if (!fineService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fineService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
