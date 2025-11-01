package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly;

import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import javafx.application.Platform;

public class AuthorizationTokenIsNotActiveError extends Error {

    public AuthorizationTokenIsNotActiveError() {
        AlertWindows.showError(
                "Неактивный токен авторизации",
                "Чтоб продолжить пользоваться программой необходимо активировать токен",
                true
        );
        Platform.exit();
        System.exit(0);
    }

}
