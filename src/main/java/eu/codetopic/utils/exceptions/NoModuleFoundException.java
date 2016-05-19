package eu.codetopic.utils.exceptions;

public class NoModuleFoundException extends RuntimeException {

    public NoModuleFoundException() {
    }

    public NoModuleFoundException(String detailMessage) {
        super(detailMessage);
    }

    public NoModuleFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoModuleFoundException(Throwable throwable) {
        super(throwable);
    }
}
