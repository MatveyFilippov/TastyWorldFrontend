package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ScaleChecker extends ExternalModuleChecker {

    private final ObjectProperty<ScaleState> actualScaleState = new SimpleObjectProperty<>();
    private boolean isChecking;

    @Override
    public void check() {
        actualScaleState.addListener((observable, oldValue, newValue) -> {
            if (isChecking && newValue.STATUS == ScaleState.Status.STABLE) {
                isChecking = false;
            }
        });

        try (ScaleManager scaleManager = new ScaleManager(actualScaleState::set)) {
            isChecking = DialogWindows.askBool("Да", "Нет", "Проверка весов", "Хотите проверить весы?", "Не обязательное действие");
            if (!isChecking) {
                return;
            }
            AlertWindows.showInfo(
                    "Прежде чем продолжить положите что-нибудь на весы и нажмите 'OK'",
                    "После этого проверка начнётся",
                    true
            );
            scaleManager.proceed();
            while (isChecking) {
                Thread.sleep(750);
            }
            scaleManager.pause();
            ScaleState lastScaleState = actualScaleState.get();
            AlertWindows.showInfo(
                    "Зафиксированный вес: %s %s".formatted(lastScaleState.WEIGHT, lastScaleState.UNIT.name()),
                    "Проверяка весов окнчена",
                    true
            );
        } catch (ExternalModuleUnavailableException | InterruptedException ignored) {}
    }

}
