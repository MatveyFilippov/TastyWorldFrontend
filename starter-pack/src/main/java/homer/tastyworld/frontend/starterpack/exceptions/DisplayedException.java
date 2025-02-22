package homer.tastyworld.frontend.starterpack.exceptions;

import javafx.application.Platform;

public abstract class DisplayedException extends RuntimeException {

    protected DisplayedException() {
        super();
    }

    protected DisplayedException(String msg) {
        super(msg);
    }

    protected DisplayedException(Throwable throwable) {
        super(throwable);
    }

    protected DisplayedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    protected abstract void action();

    public void performAction() {
        Platform.runLater(this::action);
    }

}
