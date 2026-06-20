package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.FineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<FineEntity, Long> {
}
