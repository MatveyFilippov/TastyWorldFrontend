package homer.tastyworld.frontend.starterpack.base;

import homer.tastyworld.frontend.starterpack.api.sra.Common;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.initialization.CantInitAppLoggerException;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import org.jetbrains.annotations.Nullable;
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

    public static AppLogger getFor(Class<?> clazz) {
        return new AppLogger(LoggerFactory.getLogger(clazz));
    }

    private final Logger logger;

    private AppLogger(Logger logger) {
        this.logger = logger;
    }

    public void errorOnlyWrite(String message, @Nullable Throwable throwable) {
        if (throwable == null) {
            logger.error(message);
        } else {
            logger.error(message, throwable);
        }
    }

    public void error(String message, @Nullable Throwable throwable) {
        errorOnlyWrite(message, throwable);
        Common.postError(message, throwable);
    }

    public void warn(String message, @Nullable Throwable throwable) {
        if (throwable == null) {
            logger.warn(message);
        } else {
            logger.warn(message, throwable);
        }
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void info(String message) {
        logger.info(message);
    }

}
