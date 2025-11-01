package homer.tastyworld.frontend.starterpack.base.exceptions.api.notifications;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;

public class NotificatorSubscribeException extends RuntimeException implements WithSelfUserNotificationException {

    public NotificatorSubscribeException(String message) {
        super(message);
    }

    public NotificatorSubscribeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Не могу подписаться на Notificator",
                "Разработчики получили уведомление, пока что вы не будете видеть обновления",
                true
        );
    }

}
