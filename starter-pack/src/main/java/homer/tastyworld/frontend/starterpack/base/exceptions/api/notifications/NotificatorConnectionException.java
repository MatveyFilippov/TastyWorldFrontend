package homer.tastyworld.frontend.starterpack.base.exceptions.api.notifications;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;

public class NotificatorConnectionException extends RuntimeException implements WithSelfUserNotificationException {

    public NotificatorConnectionException(Throwable throwable) {
        super("Can't connect to notificator", throwable);
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Не могу подключиться к Notificator",
                "Разработчики получили уведомление, пока что вы не будете видеть обновления",
                true
        );
    }

}
