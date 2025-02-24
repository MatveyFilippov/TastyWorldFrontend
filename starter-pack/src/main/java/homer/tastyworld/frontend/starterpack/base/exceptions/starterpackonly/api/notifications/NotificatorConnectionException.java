package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.notifications;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;

public class NotificatorConnectionException extends SelfLoggedException {

    public NotificatorConnectionException(Throwable throwable) {
        super("Notificator can't connect", throwable);
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError(messageToLog, throwableToLog);
    }

}
