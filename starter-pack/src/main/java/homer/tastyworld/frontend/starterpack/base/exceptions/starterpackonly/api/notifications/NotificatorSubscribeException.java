package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.notifications;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;

public class NotificatorSubscribeException extends SelfLoggedException {

    public NotificatorSubscribeException(String message) {
        super(message);
    }

    public NotificatorSubscribeException(Throwable throwable) {
        super("Notificator can't subscribe", throwable);
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError(messageToLog, throwableToLog);
    }

}
