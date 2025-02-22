package homer.tastyworld.frontend.starterpack.exceptions;

public abstract class UnexpectedException extends RuntimeException {

    protected UnexpectedException(String message) {
        super(message);
    }

    protected UnexpectedException(Exception ex) {
        super(ex);
    }

}
