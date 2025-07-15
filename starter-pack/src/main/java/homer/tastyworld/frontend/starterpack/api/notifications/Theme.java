package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.misc.SHA256;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;

public enum Theme {

    ORDER_STATUS_CHANGED,
    ORDER_PAID_MARKED;

    private static final String QUEUE_STARTS_WITH = String.format(
            "ClientPoint_%s.", ClientPoint.id
    );

    String getQueueName() {
        return QUEUE_STARTS_WITH + this.name() + "." + SHA256.hash(AppConfig.getToken());
    }

}
