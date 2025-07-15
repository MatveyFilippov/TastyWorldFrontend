package homer.tastyworld.frontend.starterpack;

import homer.tastyworld.frontend.starterpack.base.ErrorHandler;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.StartupPrinterCheck;
import homer.tastyworld.frontend.starterpack.base.utils.managers.scale.StartupScaleCheck;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public abstract class TastyWorldApplication extends Application {

    protected abstract Scene getScene() throws IOException;

    protected String getFullScreenExitHint() { return ""; }

    protected void onEscape(Stage stage) {
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler::appErrorHandler);
        AppConfig.init(getClass());
        CacheManager.setIsCacheAvailable(AppConfig.isCacheAvailable());
        StartupPrinterCheck.check();
        StartupScaleCheck.check();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                boolean isClose = DialogWindow.askBool(
                        "Да", "Нет", "Завершение работы", "Выйти?",
                        "Вы хотите завершить работу программы и выйти в операционную систему?"
                );
                if (isClose) {
                    onEscape(stage);
                }
            }
        });
        stage.setScene(scene);
        stage.setOnCloseRequest(ignored -> onEscape(stage));
        stage.setTitle(AppConfig.getAppTitle());
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.setFullScreenExitHint(getFullScreenExitHint());
        stage.show();
    }

}
