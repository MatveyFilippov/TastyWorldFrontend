package homer.tastyworld.frontend.starterpack;

import homer.tastyworld.frontend.starterpack.base.ErrorHandler;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.AuthorizationTokenIsNotActiveError;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import homer.tastyworld.frontend.starterpack.api.sra.entity.current.AuthorizationToken;
import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public abstract class TastyWorldApplication extends Application {

    static {
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler::appErrorHandler);
    }

    private void checkClientPointAndAuthorizationToken() {
        if (!AuthorizationToken.isActive()) {
            throw new AuthorizationTokenIsNotActiveError();
        }
        if (ClientPoint.getSubscriptionDays() < 0) {
            throw new SubscriptionDaysAreOverError();
        }
    }

    protected abstract Scene getScene() throws IOException;

    protected String getFullScreenExitHint() { return ""; }

    protected void onEscape(Stage stage) {
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public final void init() {
        AppConfig.init(getClass());
    }

    @Override
    public final void start(Stage stage) throws IOException {
        checkClientPointAndAuthorizationToken();
        ExternalModuleChecker.checkAll();

        Scene scene = getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                boolean isClose = DialogWindows.askBool(
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
