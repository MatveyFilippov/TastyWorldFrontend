package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.base.exceptions.api.notifications.NotificatorConnectionException;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.notifications.NotificatorSubscribeException;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class SubscribeClient {

    @FunctionalInterface
    interface NotificationProcessor { void process(String message); }

    private static final AppLogger logger = AppLogger.getFor(SubscribeClient.class);
    private final ConnectionFactory factory;
    private final Map<String, Channel> channels = new HashMap<>();
    private final Map<String, NotificationProcessor> queues = new HashMap<>();
    private Connection connection;

    public SubscribeClient() {
        factory = new ConnectionFactory();
        factory.setHost(AppConfig.TW_MN_HOST);
        factory.setPort(AppConfig.TW_MN_PORT);
        factory.setUsername(SHA256.hash(AppConfig.getAuthorizationTokenSRA()));
        factory.setPassword(AppConfig.getAuthorizationTokenSRA());
        factory.setVirtualHost(AppConfig.TW_MN_VHOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        factory.setRequestedHeartbeat(60);
        reconnect();
    }

    public void reconnect() {
        disconnect();

        try {
            connection = factory.newConnection();
        } catch (Exception ex) {
            throw new NotificatorConnectionException(ex);
        }

        queues.forEach(this::subscribe);
    }

    private void consume(Channel channel, String queue, DefaultConsumer consumer) {
        try {
            channel.basicConsume(queue, true, consumer);
        } catch (Exception ex) {
            throw new NotificatorSubscribeException("Client can't consume", ex);
        }
    }

    public void subscribe(String queue, NotificationProcessor processor) {
        if (channels.containsKey(queue)) {
            logger.warn("Client already subscribed on " + queue);
            return;
        }

        Channel channel;
        try {
            channel = connection.createChannel();
        } catch (Exception ex) {
            throw new NotificatorSubscribeException("Client can't createDir channel", ex);
        }

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                processor.process(new String(body, StandardCharsets.UTF_8));
            }
        };

        consume(channel, queue, consumer);

        channels.put(queue, channel);
        queues.put(queue, processor);
    }

    public void closeChannels() {
        channels.values().forEach(channel -> {
            try {
                channel.close();
            } catch (Exception ex) {
                logger.warn("Can't close chanel", ex);
            }
        });
        channels.clear();
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (NullPointerException ignored) {} catch (Exception ex) {
            logger.warn("Can't close connection", ex);
        }
    }

    public void disconnect() {
        closeChannels();
        closeConnection();
    }

    public boolean isAlive(String queue) {
        Channel channel = channels.get(queue);
        if (channel == null) {
            return false;
        }
        return channel.isOpen();
    }

}
