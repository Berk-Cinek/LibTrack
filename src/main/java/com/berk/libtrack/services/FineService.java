package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.FineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FineService {


    FineEntity partialUpdate(Long id, FineEntity fineEntity);

    FineEntity save(FineEntity fineEntity);

    Boolean isExists(Long id);

    void delete(Long id);

    List<FineEntity> findAll();

    Page<FineEntity> findAll(Pageable pageable);

    Optional<FineEntity> findOne(Long id);
}
