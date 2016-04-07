package cz.codetopic.utils.exceptions;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
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
