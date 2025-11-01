package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import java.io.IOException;

public class StatusDisplayApplication extends TastyWorldApplication {

    public static void main(String[] args) { launch(); }

    @Override
    protected Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusDisplayApplication.class.getResource("tastyworld-statusdisplay.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setCursor(Cursor.NONE);
        return scene;
    }

    @Override
    protected String getFullScreenExitHint() {
        long subscriptionAvailableDays = ClientPoint.getSubscriptionDays();
        return subscriptionAvailableDays <= 7
               ? "До окончания подписки осталось " + TypeChanger.toDaysFormat(subscriptionAvailableDays)
               : "";
    }

}
