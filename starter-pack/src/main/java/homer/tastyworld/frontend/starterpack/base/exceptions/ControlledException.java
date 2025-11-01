package homer.tastyworld.frontend.starterpack.base.exceptions;

public abstract class ControlledException extends Exception {

    protected ControlledException(String message) {
        super(message);
    }

    protected ControlledException(Throwable throwable) {
        super(throwable);
    }

    protected ControlledException(String message, Throwable cause) {
        super(message, cause);
    }

}
