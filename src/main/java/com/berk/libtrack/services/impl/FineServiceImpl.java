package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.repositories.FineRepository;
import com.berk.libtrack.services.FineService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FineServiceImpl implements FineService {

    private FineRepository fineRepository;

    public FineServiceImpl(FineRepository fineRepository) {

        this.fineRepository = fineRepository;
    }

    @Override
    @CachePut(value = "FINE_CACHE", key = "#result.id()" )
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
    @CachePut(value = "FINE_CACHE", key = "#result.id()" )
    public FineEntity save(FineEntity fineEntity) {
        return fineRepository.save(fineEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return fineRepository.existsById(id);
    }

    @Override
    @CacheEvict(value = "FINE_CACHE", key = "#id")
    public void delete(Long id) {
        fineRepository.deleteById(id);
    }

    @Override
    public List<FineEntity> findAll() {
        return fineRepository.findAll();
    }

    @Override
    public Page<FineEntity> findAll(Pageable pageable) {
        return fineRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "FINE_CHACE", key = "#id")
    public Optional<FineEntity> findOne(Long id) {
        return fineRepository.findById(id);
    }
}
