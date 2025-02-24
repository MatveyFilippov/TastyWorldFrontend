package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.init;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;

public class CantInitSHA256 extends SelfLoggedException {

    public CantInitSHA256(Throwable throwable) {
        super("Can't init sha256 algorithm", throwable);
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError(messageToLog, throwableToLog);
    }

}
