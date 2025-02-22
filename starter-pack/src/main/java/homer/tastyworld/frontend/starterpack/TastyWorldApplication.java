package homer.tastyworld.frontend.starterpack;

import homer.tastyworld.frontend.starterpack.base.ErrorHandler;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import javafx.application.Application;

public abstract class TastyWorldApplication extends Application {

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler::appErrorHandler);
        AppConfig.init(getClass());
    }

}
