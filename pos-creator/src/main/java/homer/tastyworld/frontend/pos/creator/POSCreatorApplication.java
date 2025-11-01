package homer.tastyworld.frontend.pos.creator;

import homer.tastyworld.frontend.pos.creator.core.orders.OrderCreator;
import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class POSCreatorApplication extends TastyWorldApplication {

    public static void main(String[] args) { launch(); }

    @Override
    protected Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(POSCreatorApplication.class.getResource("tastyworld-pos-creator.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        // scene.setCursor(Cursor.NONE);
        return scene;
    }

    @Override
    protected void onEscape(Stage stage) {
        stop();
        super.onEscape(stage);
    }

    @Override
    public void stop() {
        try {
            OrderCreator.cancel();
        } catch (UnexpectedResponseStatusCodeException ignored) {}
    }

}
