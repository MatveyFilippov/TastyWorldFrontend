package homer.tastyworld.frontend.statusdisplay.base.updater;

import homer.tastyworld.frontend.starterpack.utils.config.MyParams;
import java.util.List;

public class OnStartup {

    public static void setActiveOrders() {
        ((List<Object>) MyParams.CLIENT_POINT_INFO.get("ACTIVE_ORDER_IDs")).forEach(
                orderID -> OrderUpdatesListener.process(Long.parseLong(String.valueOf(orderID)))
        );
    }

}
