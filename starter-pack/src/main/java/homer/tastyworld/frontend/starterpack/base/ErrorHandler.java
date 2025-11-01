package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.base.exceptions.ControlledException;
import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import javafx.application.Platform;

public class ErrorHandler {

    private static final AppLogger logger = AppLogger.getFor(ErrorHandler.class);

    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        String message = "An unhandled error occurred";
        boolean isUserNotificationRequired = true;

        if (throwable instanceof WithSelfUserNotificationException withSelfUserNotificationException) {
            if (Platform.isFxApplicationThread()) {
                withSelfUserNotificationException.notifyUser();
            } else {
                Platform.runLater(withSelfUserNotificationException::notifyUser);
            }
            isUserNotificationRequired = false;
        }

        if (throwable instanceof UnexpectedException) {
            message = "An unexpected error (should never happen) occurred";
        } else if (throwable instanceof ControlledException) {
            message = "An unexpected error (should be caught by developer) occurred";
        }

        logger.error(message, throwable);
        if (isUserNotificationRequired) {
            AlertWindows.showError(
                    "Произошла ошибка",
                    message + "\n" + throwable.getLocalizedMessage(),
                    true
            );
        }
    }

}
