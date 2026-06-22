package com.berk.libtrack.repositories;

import com.berk.libtrack.domain.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, PagingAndSortingRepository<BookEntity, Long>
{
}
