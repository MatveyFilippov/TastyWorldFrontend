package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import java.io.IOException;

public class POSProcessorApplication extends TastyWorldApplication {

    public static void main(String[] args) { launch(); }

    @Override
    protected Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(POSProcessorApplication.class.getResource("tastyworld-pos-processor.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
//        scene.setCursor(Cursor.NONE);
        return scene;
    }

}
