package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import java.io.IOException;

public class StatusDisplayApplication extends TastyWorldApplication {

    public static void main(String[] args) { launch(); }

    @Override
    protected Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusDisplayApplication.class.getResource("tastyworld-status-display.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setCursor(Cursor.NONE);
        return scene;
    }

    @Override
    protected String getAppTitle() {
        return "TastyWorld-StatusDisplay";
    }

    @Override
    protected String getFullScreenExitHint() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            return "Подписка закончилась. Оплатите для возобновления работы...";
        } else if (subscriptionAvailableDays <= 7) {
            return "До окончания подписки осталось " + TypeChanger.toDaysFormat(subscriptionAvailableDays);
        }
        return "";
    }

}
