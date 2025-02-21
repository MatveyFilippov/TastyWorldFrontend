package homer.tastyworld.frontend.starterpack.api.notifications;

import homer.tastyworld.frontend.starterpack.utils.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.config.MyParams;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;

public enum Theme {

    ORDER_STATUS;

    private static final String CLIENT_POINT_NAME = (String) MyParams.CLIENT_POINT_INFO.get("NAME");

    String getQueueName() {
        return CLIENT_POINT_NAME.replace(" ", "_") + "." + this.name() + "." + SHA256.hash(AppConfig.getToken());
    }

}
