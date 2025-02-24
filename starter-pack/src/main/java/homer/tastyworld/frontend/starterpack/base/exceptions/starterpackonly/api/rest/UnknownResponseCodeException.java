package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest;

import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class UnknownResponseCodeException extends SelfLoggedException {

    public final int code;

    public UnknownResponseCodeException(int code) {
        super("Unknown API response code: " + code);
        this.code = code;
    }

    @Override
    protected void logExtends() {
        logger.notifyServerAboutError("StarterPack faced with unknown response code: " + code, null);
        AlertWindow.showError(
                "Безуспешная обработка API ответа",
                "Разработчики получили уведомление, попробуйте повторить запрос позже",
                true
        );
    }

}
