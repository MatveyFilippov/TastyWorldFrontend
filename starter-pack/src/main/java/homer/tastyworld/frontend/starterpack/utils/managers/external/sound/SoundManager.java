package homer.tastyworld.frontend.starterpack.utils.managers.external.sound;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SoundManager {

    private static final AppLogger logger = AppLogger.getFor(SoundManager.class);
    private static final Clip alert;

    static {
        URL alertURL = AppConfig.isSoundUnavailable() ? null : TastyWorldApplication.class.getResource("/sounds/AlertSound.wav");
        Clip tempAlert = null;
        if (alertURL != null) {
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(alertURL)) {
                tempAlert = AudioSystem.getClip();
                tempAlert.open(audio);
            } catch (Exception ex) {
                logger.error("Something is wrong while creating AlertSound clip", ex);
            }
        }
        alert = tempAlert;
    }

    private static void play(AudioInputStream audio) {
        if (AppConfig.isSoundUnavailable()) {
            return;
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audio.close();
                    } catch (Exception ex) {
                        logger.warn("Something is wrong closing audio stream", ex);
                    }
                }
            });
            clip.start();
        } catch (LineUnavailableException | IOException ex) {
            logger.error("Something is wrong while playing sound", ex);
        }
    }

    public static void play(InputStream audio) throws UnsupportedAudioFileException, IOException {
        if (!audio.markSupported()) {
            audio = new BufferedInputStream(audio);
        }
        play(AudioSystem.getAudioInputStream(audio));
    }

    public static void play(File audio) throws UnsupportedAudioFileException, IOException {
        play(AudioSystem.getAudioInputStream(audio));
    }

    public static void play(URL audio) throws UnsupportedAudioFileException, IOException {
        play(AudioSystem.getAudioInputStream(audio));
    }

    public static void playAlert() {
        if (alert == null) {
            if (!AppConfig.isSoundUnavailable()) {
                logger.error("Try to play alert, but it is unavailable", null);
                AlertWindows.showError(
                        "Ошибка звукового сигнала", "Клип недоступен, обратитесь за помощью к разарботчикам", false
                );
            }
            return;
        }
        if (alert.isRunning()) {
            alert.stop();
        }
        alert.setFramePosition(0);
        alert.start();
    }

}
