package eu.codetopic.utils.exceptions;

public class NoAnnotationPresentException extends RuntimeException {

    public NoAnnotationPresentException() {
    }

    public NoAnnotationPresentException(String detailMessage) {
        super(detailMessage);
    }

    public NoAnnotationPresentException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoAnnotationPresentException(Throwable throwable) {
        super(throwable);
    }
}
