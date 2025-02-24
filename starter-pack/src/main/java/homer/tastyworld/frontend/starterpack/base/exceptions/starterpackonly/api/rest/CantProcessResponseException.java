package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class CantProcessResponseException extends SelfLoggedException {

    public CantProcessResponseException(Throwable throwable) {
        super("Can't process API response", throwable);
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError(messageToLog, throwableToLog);
        AlertWindow.showError(
                "Безуспешная обработка API ответа",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
