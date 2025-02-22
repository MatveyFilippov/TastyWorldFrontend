package homer.tastyworld.frontend.starterpack.base.exceptions;

import java.io.IOException;

public abstract class ControlledException extends IOException {

    protected ControlledException(String message) {
        super(message);
    }

    protected ControlledException(Exception ex) {
        super(ex);
    }

}
