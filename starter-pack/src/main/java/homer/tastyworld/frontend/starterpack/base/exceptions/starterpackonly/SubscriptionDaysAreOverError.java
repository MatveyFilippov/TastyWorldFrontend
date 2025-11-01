package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly;

import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import javafx.application.Platform;

public class SubscriptionDaysAreOverError extends Error {

    public SubscriptionDaysAreOverError() {
        AlertWindows.showError(
                "Неоплаченная подписка",
                "Чтоб продолжить пользоваться программой необходимо оплатить подписку",
                    true
        );
        Platform.exit();
        System.exit(0);
    }

}
