package cz.codetopic.utils.exceptions;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
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
