package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, PagingAndSortingRepository<MemberEntity, Long> {
}
