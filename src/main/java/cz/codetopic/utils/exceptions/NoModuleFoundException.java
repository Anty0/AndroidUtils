package cz.codetopic.utils.exceptions;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
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
