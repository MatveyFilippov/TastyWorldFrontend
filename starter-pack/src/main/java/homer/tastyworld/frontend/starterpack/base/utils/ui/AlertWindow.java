package homer.tastyworld.frontend.starterpack.base.utils.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AlertWindow {

    public static Alert create(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        if (header != null) {
            alert.setHeaderText(header);
        }
        alert.setContentText(content);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return alert;
    }

    private static void show(Alert alert, boolean wait) {
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    public static void showError(String errorName, String errorText, boolean wait) {
        if (Platform.isFxApplicationThread()) {
            show(create(Alert.AlertType.ERROR, "Error", errorName, errorText), wait);
        } else {
            Platform.runLater(() -> show(create(Alert.AlertType.ERROR, "Error", errorName, errorText), wait));
        }
    }

    public static void showInfo(String header, String content, boolean wait) {
        if (Platform.isFxApplicationThread()) {
            show(create(Alert.AlertType.INFORMATION, "Info", header, content), wait);
        } else {
            Platform.runLater(() -> show(create(Alert.AlertType.INFORMATION, "Info", header, content), wait));
        }
    }

}
