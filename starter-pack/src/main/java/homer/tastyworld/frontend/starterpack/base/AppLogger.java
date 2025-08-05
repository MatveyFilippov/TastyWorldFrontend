package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.init.CantInitAppLoggerException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import org.apache.hc.core5.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class AppLogger {

    static {
        ConsoleHandler defaultConsoleHandler = new ConsoleHandler();
        defaultConsoleHandler.setLevel(Level.ALL);
        defaultConsoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    thrown.printStackTrace(pw);
                    message += "\n" + sw;
                }
                return String.format(
                        "%s %tT - %s%n",
                        record.getLevel() == Level.SEVERE ? "ERROR" : record.getLevel().toString(),
                        record.getMillis(),
                        message
                );
            }
        });

        FileHandler defaultFileHandler;
        try {
            defaultFileHandler = new FileHandler(
                    AppConfig.APP_DATA_DIR.getAbsolutePath() + File.separator + "TastyWorldApp.log",
                    Long.MAX_VALUE, 1, true
            );
        } catch (IOException ex) {
            throw new CantInitAppLoggerException(ex);
        }
        defaultFileHandler.setLevel(Level.ALL);
        defaultFileHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    thrown.printStackTrace(pw);
                    message += "\n" + sw;
                }
                return String.format(
                        "%1$s %2$tY-%2$tm-%2$td %2$tT %3$s [%4$s] - %5$s%n",
                        record.getLevel() == Level.SEVERE ? "ERROR" : record.getLevel().toString(),
                        record.getMillis(),
                        record.getLoggerName(),
                        Thread.currentThread().getName(),
                        message
                );
            }
        });

        java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
        Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
        root.addHandler(defaultConsoleHandler);
        root.addHandler(defaultFileHandler);
        root.setLevel(Level.INFO);
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

    private final Logger logger;

    private AppLogger(Logger logger) {
        this.logger = logger;
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
        request.putInBody("app_name", AppConfig.getAppIdentifierName());
        request.putInBody("app_version", AppConfig.getAppVersion());
        request.putInBody("error", message);
        if (throwable != null) {
            request.putInBody("stack_trace", getErrorStackTrace(throwable));
        }
        Response response = request.request();
        if (!response.status().equals("200 OK")) {
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
