package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class StartupScaleCheck {

    public static void check() {
        if (!ScaleManager.IS_SCALE_AVAILABLE && AppConfig.getScaleComPort() != null) {
            AlertWindow.showError(
                    "Ошибка с весами", "Не удалось подключиться к весам, их использование будет ограничено", false
            );
        }

    }

}
