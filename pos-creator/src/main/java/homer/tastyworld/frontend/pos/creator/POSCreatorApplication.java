package homer.tastyworld.frontend.pos.creator;

import homer.tastyworld.frontend.pos.creator.core.orders.internal.OrderCreating;
import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public class POSCreatorApplication extends TastyWorldApplication {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        CacheManager.setIsCacheAvailable(true);
        FXMLLoader fxmlLoader = new FXMLLoader(POSCreatorApplication.class.getResource("tastyworld-pos-creator.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TastyWorld-POS-Creator");
        scene.setCursor(Cursor.NONE);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
                stop();
                Platform.exit();
                System.exit(0);
            }
        });
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    @Override
    public void stop() {
        try {
            OrderCreating.delete();
        } catch (BadRequestException ignored) {}
    }

}
