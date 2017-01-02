package toby.study.Exception;

/**
 * Created by blue4 on 2017-01-02.
 */
public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}
