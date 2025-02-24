package homer.tastyworld.frontend.starterpack.base.exceptions;

import javafx.application.Platform;

public abstract class DisplayedException extends RuntimeException {

    protected DisplayedException() {
        super();
    }

    protected DisplayedException(String message) {
        super(message);
    }

    protected DisplayedException(Throwable throwable) {
        super(throwable);
    }

    protected DisplayedException(String message, Throwable cause) {
        super(message, cause);
    }

    protected abstract void action();

    public void performAction() {
        if (Platform.isFxApplicationThread()) {
            action();
        } else {
            Platform.runLater(this::action);
        }
    }

}
