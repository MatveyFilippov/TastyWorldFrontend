package homer.tastyworld.frontend.starterpack.utils;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.ui.ErrorAlert;
import homer.tastyworld.frontend.starterpack.utils.config.AppConfig;
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

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void errorWithoutUserNotify(String msg, Throwable ex) {
        if (ex == null) {
            logger.error(msg);
        } else {
            logger.error(msg, ex);
        }
    }

    public void errorWithoutServerNotify(String msg, Throwable ex) {
        errorWithoutUserNotify(msg, ex);
        if (ex == null) {
            ErrorAlert.showAlert("Произошла ошибка", msg, false);
        } else {
            ErrorAlert.showAlert("Произошла ошибка", ex.getLocalizedMessage(), false);
        }
    }

    private Response notifyServerAboutError(String msg, Throwable ex) {
        Request request = new Request("/frontend_app/error", Method.POST);
        request.putInBody("app_name", AppConfig.getAppName());
        request.putInBody("app_version", AppConfig.getAppVersion());
        request.putInBody("error", msg);
        if (ex != null) {
            request.putInBody("stack_trace", getErrorStackTrace(ex));
        }
        return request.request();
    }

    public void error(String msg, Throwable ex) {
        errorWithoutUserNotify(msg, ex);
        Response response = notifyServerAboutError(msg, ex);
        if (!response.status.equals("200 OK")) {
            errorWithoutUserNotify("Can't notify server about error --- " + response, null);
            ErrorAlert.showAlert(
                    "Срочно обратитесь к разработчикам",
                    "Приложению не удаётся самостоятельно оповестить о случившейся ошибке",
                    true
            );
            return;
        }
        if (ex == null) {
            ErrorAlert.showAlert("Произошла ошибка, разработчики получили уведомление", msg, false);
        } else {
            ErrorAlert.showAlert(
                    "Произошла ошибка, разработчики получили уведомление",
                    ex.getLocalizedMessage(),
                    false
            );
        }
    }

    public void error(String msg) {
        error(msg, null);
    }

}
