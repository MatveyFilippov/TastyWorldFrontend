package homer.tastyworld.frontend.starterpack.base.utils.managers.sound;

import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class StartupSoundCheck {

    public static void check() {
        if (SoundPlayer.IS_SOUNDS_UNAVAILABLE) {
            return;
        }
        if (SoundManager.TW_ALERT_SOUND == null) {
            AlertWindow.showError(
                    "Ошибка со звуком", "Не удалось найти 'AlertSound', использование будет ограничено", false
            );
        } else {
            SoundManager.playAlert();
        }
    }

}
