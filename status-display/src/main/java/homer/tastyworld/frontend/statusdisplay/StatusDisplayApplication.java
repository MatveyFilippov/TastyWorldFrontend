package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public class StatusDisplayApplication extends TastyWorldApplication {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusDisplayApplication.class.getResource("tastyworld-status-display.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TastyWorldStatusDisplay");
        scene.setCursor(Cursor.NONE);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
                System.exit(0);
            }
        });
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.setFullScreenExitHint("Press ESC to exit");
        stage.show();
    }

}
