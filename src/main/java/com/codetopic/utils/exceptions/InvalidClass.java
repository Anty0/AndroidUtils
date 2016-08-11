package com.codetopic.utils.exceptions;

public class InvalidClass extends RuntimeException {

    public InvalidClass() {
    }

    public InvalidClass(String detailMessage) {
        super(detailMessage);
    }

    public InvalidClass(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidClass(Throwable throwable) {
        super(throwable);
    }
}
