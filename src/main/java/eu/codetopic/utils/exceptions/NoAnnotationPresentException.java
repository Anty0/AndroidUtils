package eu.codetopic.utils.exceptions;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
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
