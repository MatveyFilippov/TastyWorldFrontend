package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class NotificatorSubscribeException extends UnexpectedException {

    public NotificatorSubscribeException(Exception ex) {
        super(ex);
    }

    public NotificatorSubscribeException(String message) {
        super(message);
    }

}
