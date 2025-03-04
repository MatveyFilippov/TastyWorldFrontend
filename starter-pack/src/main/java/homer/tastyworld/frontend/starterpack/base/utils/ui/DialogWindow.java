package homer.tastyworld.frontend.starterpack.base.utils.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogWindow {

    @FunctionalInterface
    public interface AnswerChecker { boolean isGoodAnswer(String answer); }

    public static TextInputDialog getDialog(String title, String request, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(request);
        dialog.setContentText(prompt);

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return dialog;
    }

    public static String getOrNull(String title, String request, String prompt) {
        TextInputDialog dialog = getDialog(title, request, prompt);
        return dialog.showAndWait().orElse(null);
    }

    public static String askTillGood(String title, String request, String prompt, AnswerChecker checker) {
        TextInputDialog dialog = getDialog(title, request, prompt);
        while (true) {
            String result = dialog.showAndWait().orElse(null);
            if (checker.isGoodAnswer(result)) {
                return result;
            }
            AlertWindow.showError("InvalidValue", "Вы ввели плохое значение, попробуйте заново", true);
        }
    }

    public static boolean askBool(String forTrue, String forFalse, String title, String request, String prompt) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(request);
        dialog.setContentText(prompt);

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        ButtonType buttonTypeYes = new ButtonType(forTrue);
        ButtonType buttonTypeNo = new ButtonType(forFalse);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeYes, buttonTypeNo);

        return dialog.showAndWait().orElse(buttonTypeNo) == buttonTypeYes;
    }

}
