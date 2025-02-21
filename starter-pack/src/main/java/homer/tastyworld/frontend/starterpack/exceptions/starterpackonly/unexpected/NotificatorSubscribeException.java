package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.unexpected;

import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class NotificatorSubscribeException extends UnexpectedException {

    public NotificatorSubscribeException(Exception ex) {
        super(ex);
    }

    public NotificatorSubscribeException(String message) {
        super(message);
    }

}
