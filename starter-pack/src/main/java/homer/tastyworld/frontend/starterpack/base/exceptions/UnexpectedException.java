package homer.tastyworld.frontend.starterpack.base.exceptions;

public abstract class UnexpectedException extends RuntimeException {

    protected UnexpectedException(String message) {
        super(message);
    }

    protected UnexpectedException(Throwable throwable) {
        super(throwable);
    }

    protected UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
