package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.time.Duration;
import java.util.EnumSet;
import java.util.function.Consumer;

public class Subscriber {

    private static final AppLogger logger = AppLogger.getFor(Subscriber.class);
    private static final SubscribeClient client = new SubscribeClient();
    private static final EnumSet<Theme> themes = EnumSet.noneOf(Theme.class);
    private static boolean isAliveChecking = false;

    private static String getQueueFromTheme(Theme theme) {
        return "ClientPoint_%s.%s.%s".formatted(ClientPoint.id, theme.name(), SHA256.hash(AppConfig.getAuthorizationTokenSRA()));
    }

    public static void subscribe(Theme theme, Consumer<String> onNotification) {
        client.subscribe(getQueueFromTheme(theme), message -> Platform.runLater(() -> onNotification.accept(message)));
        themes.add(theme);
        startAliveChecking();
    }

    private static void startAliveChecking() {
        if (isAliveChecking) {
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                while (true) {
                    if (themes.stream().anyMatch(theme -> !client.isAlive(getQueueFromTheme(theme)))) {
                        logger.warn("Connection for some queue is failed while running -> try to reconnect");
                        client.reconnect();
                    }
                    try {
                        Thread.sleep(Duration.ofMinutes(5));
                    } catch (InterruptedException ex) {
                        logger.error("Thread for checking subscriptions alive is interrupted", ex);
                        isAliveChecking = false;
                        return null;
                    }
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setName("SubscribeAliveChecker");
        thread.setDaemon(true);
        thread.start();
        isAliveChecking = true;
    }

}
