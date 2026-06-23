package com.berk.libtrack.exceptions;

public class BorrowingNotAllowedException extends RuntimeException{

    public BorrowingNotAllowedException(String message) {
        super(message);
    }
}
