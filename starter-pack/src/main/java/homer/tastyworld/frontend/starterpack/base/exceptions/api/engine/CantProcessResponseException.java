package homer.tastyworld.frontend.starterpack.base.exceptions.api.engine;

import homer.tastyworld.frontend.starterpack.base.exceptions.WithSelfUserNotificationException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class CantProcessResponseException extends RuntimeException implements WithSelfUserNotificationException {

    public final ClassicHttpResponse response;

    public CantProcessResponseException(ClassicHttpResponse response, Throwable throwable) {
        super("Can't process API response", throwable);
        this.response = response;
    }

    @Override
    public void notifyUser() {
        AlertWindows.showError(
                "Безуспешная обработка API ответа",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
