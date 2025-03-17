package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public class POSProcessorApplication extends TastyWorldApplication {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // CacheManager.setIsCacheAvailable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(POSProcessorApplication.class.getResource("tastyworld-pos-processor.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TastyWorld-POS-Processor");
//        scene.setCursor(Cursor.NONE);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
                Platform.exit();
                System.exit(0);
            }
        });
        stage.setScene(scene);
//        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

}
