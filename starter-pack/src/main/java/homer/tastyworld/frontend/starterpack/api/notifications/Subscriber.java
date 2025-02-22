package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.utils.AppLogger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class Subscriber {

    @FunctionalInterface
    public interface NotificationProcessor { void process(String message); }

    private static final AppLogger logger = AppLogger.getFor(Subscriber.class);
    private static final SubscribeClient client = new SubscribeClient();
    private static final Set<String> queues = new HashSet<>();
    private static boolean isAliveChecking = false;

    public static void subscribe(Theme theme, NotificationProcessor processor) {
        String queue = theme.getQueueName();
        client.subscribe(queue, message -> Platform.runLater(() -> processor.process(message)));
        queues.add(queue);
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
                    if (queues.stream().anyMatch(queue -> !client.isAlive(queue))) {
                        logger.error("Connection for some queue is failed while running -> try to reconnect");
                        client.reconnect();
                    }
                    try {
                        Thread.sleep(Duration.ofMinutes(5));
                    } catch (InterruptedException ex) {
                        logger.errorWithoutUserNotify("Thread for checking subscriptions alive is interrupted", ex);
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
