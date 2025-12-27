package com.myproject.mini_board.global.exception;

public class DifferentUserException extends RuntimeException {
    public DifferentUserException() {
        super();
    }

    public DifferentUserException(String message) {
        super(message);
    }

    public DifferentUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
