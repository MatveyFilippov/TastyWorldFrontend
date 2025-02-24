package homer.tastyworld.frontend.starterpack.base.exceptions;

import homer.tastyworld.frontend.starterpack.base.AppLogger;

public abstract class SelfLoggedException extends RuntimeException {

    protected final AppLogger logger = AppLogger.getFor(getClass());
    protected final String messageToLog;
    protected final Throwable throwableToLog;

    protected SelfLoggedException(String message) {
        super(message);
        messageToLog = message;
        throwableToLog = null;
        log();
    }

    protected SelfLoggedException(String message, Throwable cause) {
        super(message, cause);
        messageToLog = message;
        throwableToLog = cause;
        log();
    }

    private void log() {
        logger.errorOnlyWrite(messageToLog, throwableToLog == null ? getCause() : throwableToLog);
        logExtends();
    }

    protected abstract void logExtends();

}
