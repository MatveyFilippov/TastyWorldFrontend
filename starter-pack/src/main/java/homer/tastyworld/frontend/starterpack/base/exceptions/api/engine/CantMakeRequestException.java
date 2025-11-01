package homer.tastyworld.frontend.starterpack.base.exceptions.api.engine;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;

public class CantMakeRequestException extends RuntimeException implements WithSelfUserNotificationException {

    public final Request request;

    public CantMakeRequestException(Request request, Throwable throwable) {
        super("Can't make request: " + request, throwable);
        this.request = request;
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Безуспешный API запрос",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
