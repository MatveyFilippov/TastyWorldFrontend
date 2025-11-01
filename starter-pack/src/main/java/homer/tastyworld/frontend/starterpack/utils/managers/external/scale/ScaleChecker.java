package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ScaleChecker extends ExternalModuleChecker {

    class WeightWindow {

        private final Stage stage;
        private final Label weightLabel;

        public WeightWindow(Runnable okCallback) {
            weightLabel = new Label("Положите что-нибудь на весы");
            weightLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

            Button okButton = new Button("OK");
            okButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");

            VBox layout = new VBox(15);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));
            layout.getChildren().addAll(weightLabel, okButton);

            Stage owner = (Stage) Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);

            stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Проверка весов");
            stage.setScene(new Scene(layout, 300, 150));
            stage.setResizable(false);

            okButton.setOnAction(e -> {
                if (okCallback != null) {
                    okCallback.run();
                }
                stage.close();
            });
        }

        public void show() {
            stage.show();
        }

        public void setWeight(String text) {
            weightLabel.setText(text);
        }

    }

    private volatile boolean isChecking = false;

    @Override
    public void check() {
        try(ScaleManager scaleManager = new ScaleManager()) {
            isChecking = DialogWindows.askBool("Да", "Нет", "Проверка весов", "Хотите проверить весы?", "Не обязательное действие");
            if (!isChecking) {
                return;
            }
            WeightWindow weightWindow = new WeightWindow(() -> isChecking = false);
            weightWindow.show();
            while (isChecking) {
                ScaleState state = scaleManager.getScaleState();
                if (state.STATUS == ScaleState.Status.STABLE) {
                    String weight = state.WEIGHT + " " + state.UNIT.name().toLowerCase();
                    weightWindow.setWeight(weight);
                }
            }
        } catch (ExternalModuleUnavailableException | InterruptedException ignored) {}
    }

}
