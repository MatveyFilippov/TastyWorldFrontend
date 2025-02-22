package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.misc.SHA256;

public enum Theme {

    ORDER_STATUS;

    private static final String CLIENT_POINT_NAME = (String) MyParams.getClientPointInfo().get("NAME");

    String getQueueName() {
        return CLIENT_POINT_NAME.replace(" ", "_") + "." + this.name() + "." + SHA256.hash(AppConfig.getToken());
    }

}
