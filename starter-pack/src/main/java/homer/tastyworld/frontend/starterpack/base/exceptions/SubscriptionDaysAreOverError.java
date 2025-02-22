package homer.tastyworld.frontend.starterpack.base.exceptions;

import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import javafx.application.Platform;

public class SubscriptionDaysAreOverError extends Error {

    public SubscriptionDaysAreOverError() {
        AlertWindow.showError(
                "Неоплаченная подписка",
                "Чтоб продолжить пользоваться программой необходимо оплатить подписку",
                    true
        );
        Platform.exit();
        System.exit(0);
    }

}
