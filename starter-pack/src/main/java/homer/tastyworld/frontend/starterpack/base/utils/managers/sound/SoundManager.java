package homer.tastyworld.frontend.starterpack.base.utils.managers.sound;

import homer.tastyworld.frontend.starterpack.TastyWorldApplication;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class SoundManager {

    private static final AppLogger logger = AppLogger.getFor(SoundManager.class);
    protected static final URL TW_ALERT_SOUND = TastyWorldApplication.class.getResource("/sounds/AlertSound.wav");
    private static final CacheProcessor<String, Clip> clipCache = CacheManager.register(
            url -> {
                Clip clip = null;
                try {
                    AudioInputStream audio = AudioSystem.getAudioInputStream(URI.create(url).toURL());
                    clip = AudioSystem.getClip();
                    clip.open(audio);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                    logger.errorOnlyServerNotify("Something is wrong while creating sound clip", ex);
                }
                return clip;
            }
    );

    public static void play(InputStream audio) throws UnsupportedAudioFileException, IOException {
        if (!audio.markSupported()) {
            audio = new BufferedInputStream(audio);
        }
        SoundPlayer.play(AudioSystem.getAudioInputStream(audio));
    }

    public static void play(File audio) throws UnsupportedAudioFileException, IOException {
        SoundPlayer.play(AudioSystem.getAudioInputStream(audio));
    }

    public static void play(URL audio) throws UnsupportedAudioFileException, IOException {
        SoundPlayer.play(AudioSystem.getAudioInputStream(audio));
    }

    public static void playWithCache(URL audio) {
        Clip clip = clipCache.get(audio.toString());
        if (clip != null) {
            SoundPlayer.play(clip, true);
        }
    }

    public static void playAlert() {
        if (TW_ALERT_SOUND != null) {
            playWithCache(TW_ALERT_SOUND);
        }
    }

}
