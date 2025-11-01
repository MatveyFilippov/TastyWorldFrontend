package homer.tastyworld.frontend.starterpack.base.exceptions.controlled;

import homer.tastyworld.frontend.starterpack.base.exceptions.ControlledException;

public class ExternalModuleUnavailableException extends ControlledException {

    public ExternalModuleUnavailableException(String message) {
        super(message);
    }

    public ExternalModuleUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

}
