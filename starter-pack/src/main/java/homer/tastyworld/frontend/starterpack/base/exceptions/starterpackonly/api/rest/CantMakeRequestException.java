package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import org.apache.hc.core5.http.Method;

public class CantMakeRequestException extends SelfLoggedException {

    public CantMakeRequestException(Method method, String url, String token, String jsonBody, Throwable throwable) {
        super(String.format(
                "Can't make %s request on '%s' with (token: '%s'; jsonBody: %s)",
                method.name(), url, token, jsonBody
        ), throwable);
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError(messageToLog, throwableToLog);
        AlertWindow.showError(
                "Безуспешный API запрос",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
