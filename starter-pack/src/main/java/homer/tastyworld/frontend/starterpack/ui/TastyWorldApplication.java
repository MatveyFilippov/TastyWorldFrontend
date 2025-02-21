package homer.tastyworld.frontend.starterpack.ui;

import homer.tastyworld.frontend.starterpack.utils.ErrorHandler;
import homer.tastyworld.frontend.starterpack.utils.config.AppConfig;
import javafx.application.Application;

public abstract class TastyWorldApplication extends Application {

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler::appErrorHandler);
        AppConfig.init(getClass());
    }

}
