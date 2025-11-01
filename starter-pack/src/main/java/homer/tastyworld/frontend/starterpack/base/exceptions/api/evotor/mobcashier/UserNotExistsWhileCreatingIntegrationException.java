package homer.tastyworld.frontend.starterpack.base.exceptions.api.evotor.mobcashier;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;

public class UserNotExistsWhileCreatingIntegrationException extends RuntimeException implements WithSelfUserNotificationException {

    public UserNotExistsWhileCreatingIntegrationException() {
        super("Can't create Evotor Mobile-Cashier integration because user does not exists");
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Неверные данные для Evotor Mobile-Cashier",
                "Пользователь не зарегистрирован в личном кабинете",
                true
        );
    }

}
