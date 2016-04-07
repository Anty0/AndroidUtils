package cz.codetopic.utils.exceptions;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class NoAnnotationException extends RuntimeException {

    public NoAnnotationException() {
    }

    public NoAnnotationException(String detailMessage) {
        super(detailMessage);
    }

    public NoAnnotationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoAnnotationException(Throwable throwable) {
        super(throwable);
    }
}
