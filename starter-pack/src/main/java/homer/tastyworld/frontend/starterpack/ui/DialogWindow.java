package homer.tastyworld.frontend.starterpack.ui;

import javafx.scene.control.TextInputDialog;

public class DialogWindow {

    @FunctionalInterface
    public interface AnswerChecker { boolean isGoodAnswer(String answer); }

    public static TextInputDialog getDialog(String title, String request, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(request);
        dialog.setContentText(prompt);
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
            ErrorAlert.showAlert(
                    "InvalidValue", "Вы ввели плохое значение, попробуйте заново", true
            );
        }
    }

}
