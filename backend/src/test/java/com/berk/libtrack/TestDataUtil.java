package com.berk.libtrack;

import com.berk.libtrack.domain.entities.BookEntity;

public final class TestDataUtil {

    public TestDataUtil() {
    }

    public static BookEntity createBookEntity(){
        return BookEntity.builder()
                .isbn(123371L)
                .title("the myth of sysphius")
                .author("albert camus")
                .genre("philoshopy")
                .totalCopies(68)
                .availableCopies(20)
                .build();
    }
}
