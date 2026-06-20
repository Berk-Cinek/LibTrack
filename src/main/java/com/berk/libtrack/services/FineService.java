package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.FineEntity;

public interface FineService {

    FineEntity partialUpdate(Long id, FineEntity fineEntity);

    FineEntity save(FineEntity fineEntity);

    Boolean isExists(Long id);

    void delete(Long id);
}
