package homer.tastyworld.frontend.starterpack.base.exceptions;

import java.io.IOException;

public abstract class ControlledException extends IOException {

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
