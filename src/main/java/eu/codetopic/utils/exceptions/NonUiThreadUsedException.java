package eu.codetopic.utils.exceptions;

/**
 * Created by anty on 15.3.16.
 *
 * @author anty
 */
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
