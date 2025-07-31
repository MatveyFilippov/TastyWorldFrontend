package homer.tastyworld.frontend.starterpack.base.utils.managers.sound;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

class SoundPlayer {

    private static final AppLogger logger = AppLogger.getFor(SoundPlayer.class);
    public static final boolean IS_SOUNDS_UNAVAILABLE = AppConfig.isSoundsUnavailable();

    public static void play(Clip clip, boolean isResetToStartBeforePlayRequired) {
        if (IS_SOUNDS_UNAVAILABLE) {
            return;
        }
        if (isResetToStartBeforePlayRequired) {
            clip.setFramePosition(0);
        }
        clip.start();
    }

    public static void play(AudioInputStream audio) {
        if (IS_SOUNDS_UNAVAILABLE) {
            return;
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(audio);
            clip.start();
        } catch (LineUnavailableException | IOException ex) {
            logger.errorOnlyServerNotify("Something is wrong while playing sound", ex);
        }
    }

}
