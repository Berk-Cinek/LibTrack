package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.repositories.FineRepository;
import com.berk.libtrack.services.FineService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FineServiceImpl implements FineService {

    private FineRepository fineRepository;

    public FineServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Override
    public FineEntity partialUpdate(Long id, FineEntity fineEntity) {
        fineEntity.setId(id);

        return fineRepository.findById(id).map(existingFine ->{
            Optional.ofNullable(fineEntity.getLoanEntity()).ifPresent(existingFine::setLoanEntity);
            Optional.ofNullable(fineEntity.getDaysOverdue()).ifPresent(existingFine::setDaysOverdue);
            Optional.ofNullable(fineEntity.getAmount()).ifPresent(existingFine::setAmount);
            Optional.ofNullable(fineEntity.getIsPaid()).ifPresent(existingFine::setIsPaid);
            Optional.ofNullable(fineEntity.getPaidAt()).ifPresent(existingFine::setPaidAt);
            return fineRepository.save(existingFine);
        }).orElseThrow(() -> new RuntimeException("Non Existing Fine"));
    }

    @Override
    public FineEntity save(FineEntity fineEntity) {
        return fineRepository.save(fineEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return fineRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        fineRepository.deleteById(id);
    }
}
