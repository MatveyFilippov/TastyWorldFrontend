package homer.tastyworld.frontend.poscreator;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
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
        FXMLLoader fxmlLoader = new FXMLLoader(POSCreatorApplication.class.getResource("tastyworld-pos-creator.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TastyWorld-POS-Creator");
//        scene.setCursor(Cursor.NONE);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.close();
                System.exit(0);
            }
        });
        stage.setScene(scene);
//        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.setFullScreenExitHint(null);
        stage.show();
    }

}
