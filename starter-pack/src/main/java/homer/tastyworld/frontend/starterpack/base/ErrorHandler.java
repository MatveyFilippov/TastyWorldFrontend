package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.base.exceptions.ControlledException;
import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class ErrorHandler {

    private static final AppLogger logger = AppLogger.getFor(ErrorHandler.class);

    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        if (throwable instanceof SelfLoggedException) {
            // ignore
        } else if (throwable instanceof DisplayedException) {
            ((DisplayedException) throwable).performAction();
        } else if (throwable instanceof UnexpectedException) {
            logger.error("An unexpected error (should never happen) occurred", throwable);
        } else if (throwable instanceof ControlledException) {
            logger.error("An unexpected error (should be caught by developer)occurred", throwable);
        } else {
            logger.error("An unhandled error occurred", throwable);
        }
    }

}
