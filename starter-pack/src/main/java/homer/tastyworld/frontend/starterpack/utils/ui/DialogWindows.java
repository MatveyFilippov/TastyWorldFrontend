package homer.tastyworld.frontend.starterpack.utils.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class DialogWindows {

    public static TextInputDialog getDialog(String title, String question, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(question);
        dialog.setContentText(prompt);

        dialog.initModality(Modality.APPLICATION_MODAL);
        // dialog.initStyle();
        dialog.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return dialog;
    }

    public static Optional<String> get(String title, String question, String prompt) {
        TextInputDialog dialog = getDialog(title, question, prompt);
        return dialog.showAndWait();
    }

    public static String askTillGood(String title, String question, String prompt, Predicate<String> isGoodAnswer) {
        TextInputDialog dialog = getDialog(title, question, prompt);
        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && isGoodAnswer.test(result.get())) {
                return result.get();
            }
            AlertWindows.showError("InvalidValue", "Вы ввели плохое значение, попробуйте заново", true);
        }
    }

    public static boolean askBool(String forTrue, String forFalse, String title, String question, String prompt) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(question);
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
