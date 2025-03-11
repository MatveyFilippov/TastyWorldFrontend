package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.misc.SHA256;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;

public enum Theme {

    ORDER_STATUS_CHANGED,
    ORDER_PAID_MARKED;

    private static final String QUEUE_STARTS_WITH = String.format(
            "ClientPoint_%s.", TypeChanger.toLong(MyParams.getClientPointInfo().get("ID"))
    );

    String getQueueName() {
        return QUEUE_STARTS_WITH + this.name() + "." + SHA256.hash(AppConfig.getToken());
    }

}
