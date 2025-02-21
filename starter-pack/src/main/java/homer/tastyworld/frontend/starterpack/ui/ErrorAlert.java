package homer.tastyworld.frontend.starterpack.ui;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ErrorAlert {

    public static Alert createAlert(String errorName, String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(errorName);
        alert.setContentText(errorText);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return alert;
    }

    public static void showAlert(String errorName, String errorText, boolean wait) {
        Alert alert = createAlert(errorName, errorText);
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

}
