package homer.tastyworld.frontend.starterpack.utils;

import homer.tastyworld.frontend.starterpack.exceptions.ControlledException;
import homer.tastyworld.frontend.starterpack.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.exceptions.UnexpectedException;

public class ErrorHandler {

    private static final AppLogger logger = AppLogger.getFor(ErrorHandler.class);

    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        if (throwable instanceof DisplayedException) {
            // ignored
        } else if (throwable instanceof UnexpectedException) {
            logger.error("An unexpected error (should never happen) occurred", throwable);
        } else if (throwable instanceof ControlledException) {
            logger.error("An unexpected error (should be caught by developer)occurred", throwable);
        } else {
            logger.error("An unhandled error occurred", throwable);
        }
    }

}
