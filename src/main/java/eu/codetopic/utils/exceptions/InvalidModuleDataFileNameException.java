package eu.codetopic.utils.exceptions;

public class InvalidModuleDataFileNameException extends Exception {

    public InvalidModuleDataFileNameException() {
    }

    public InvalidModuleDataFileNameException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidModuleDataFileNameException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidModuleDataFileNameException(Throwable throwable) {
        super(throwable);
    }
}
