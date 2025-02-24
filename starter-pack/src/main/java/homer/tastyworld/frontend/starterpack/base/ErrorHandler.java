package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.base.exceptions.ControlledException;
import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class ErrorHandler {

    private static final AppLogger logger = AppLogger.getFor(ErrorHandler.class);

    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        for (Throwable temp = throwable; temp != null; temp = temp.getCause()) {
            if (temp instanceof SelfLoggedException) {
                return;
            } else if (temp instanceof DisplayedException) {
                ((DisplayedException) temp).performAction();
                return;
            } else if (temp instanceof UnexpectedException) {
                logger.error("An unexpected error (should never happen) occurred", throwable);
                return;
            } else if (temp instanceof ControlledException) {
                logger.error("An unexpected error (should be caught by developer) occurred", throwable);
                return;
            }
        }
        logger.error("An unhandled error occurred", throwable);
    }

}
