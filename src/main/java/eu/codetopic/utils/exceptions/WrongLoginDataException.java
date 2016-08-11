package eu.codetopic.utils.exceptions;

import java.net.ConnectException;

public class WrongLoginDataException extends ConnectException {

    public WrongLoginDataException() {
        super();
    }

    public WrongLoginDataException(String detailMessage) {
        super(detailMessage);
    }
}
