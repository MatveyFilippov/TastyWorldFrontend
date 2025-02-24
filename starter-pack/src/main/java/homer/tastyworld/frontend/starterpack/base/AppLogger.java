package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import org.apache.hc.core5.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AppLogger {

    public static final AppLogger GLOBAL_LOGGER = AppLogger.getFor(AppLogger.class);
    private final Logger logger;

    private AppLogger(Logger logger) {
        this.logger = logger;
    }

    private static String getErrorStackTrace(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static AppLogger getFor(Class<?> clazz) {
        return new AppLogger(LoggerFactory.getLogger(clazz));
    }

    public void errorOnlyWrite(String message, Throwable throwable) {
        if (throwable == null) {
            logger.error(message);
        } else {
            logger.error(message, throwable);
        }
    }

    private void notifyUserAboutError(String errorName, String message, Throwable throwable) {
        if (throwable == null) {
            AlertWindow.showError(errorName, message, true);
        } else {
            AlertWindow.showError(errorName, throwable.getLocalizedMessage(), true);
        }
    }

    public void notifyServerAboutError(String message, Throwable throwable) {
        Request request = new Request("/frontend_app/error", Method.POST);
        request.putInBody("app_name", AppConfig.getAppName());
        request.putInBody("app_version", AppConfig.getAppVersion());
        request.putInBody("error", message);
        if (throwable != null) {
            request.putInBody("stack_trace", getErrorStackTrace(throwable));
        }
        Response response = request.request();
        if (!response.status.equals("200 OK")) {
            errorOnlyWrite("Can't notify server about error --- " + response, null);
            notifyUserAboutError(
                    "Срочно обратитесь к разработчикам",
                    "Приложению не удаётся самостоятельно оповестить о случившейся ошибке",
                    null
            );
        }
    }

    public void errorOnlyServerNotify(String message, Throwable throwable) {
        errorOnlyWrite(message, throwable);
        notifyServerAboutError(message, throwable);
    }

    public void errorOnlyUserNotify(String message, Throwable throwable) {
        errorOnlyWrite(message, throwable);
        notifyUserAboutError("Произошла ошибка", message, throwable);
    }

    public void error(String message, Throwable throwable) {
        errorOnlyWrite(message, throwable);
        notifyServerAboutError(message, throwable);
        notifyUserAboutError("Произошла ошибка", message, throwable);
    }

    public void error(String message) {
        error(message, null);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void info(String message) {
        logger.info(message);
    }

}
