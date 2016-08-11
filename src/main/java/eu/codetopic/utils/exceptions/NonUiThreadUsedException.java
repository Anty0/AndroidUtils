package eu.codetopic.utils.exceptions;

public class NonUiThreadUsedException extends RuntimeException {

    public NonUiThreadUsedException() {
    }

    public NonUiThreadUsedException(String detailMessage) {
        super(detailMessage);
    }

    public NonUiThreadUsedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NonUiThreadUsedException(Throwable throwable) {
        super(throwable);
    }
}
