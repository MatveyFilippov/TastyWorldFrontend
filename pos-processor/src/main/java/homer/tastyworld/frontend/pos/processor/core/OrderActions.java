package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterManager;
import org.apache.hc.core5.http.Method;

public class OrderActions {

    private static final Request PROCESSING_REQUEST = new Request("/order/set_status", Method.POST);
    private static final Request PROCESSED_REQUEST = new Request("/order/set_status", Method.POST);

    static {
        PROCESSING_REQUEST.putInBody("new_status", "PROCESSING");
        PROCESSED_REQUEST.putInBody("new_status", "PROCESSED");
    }

    public static void setProcessing(long orderID) {
        PROCESSING_REQUEST.putInBody("id", orderID);
        PROCESSING_REQUEST.request();
    }

    public static void setProcessed(long orderID) {
        PROCESSED_REQUEST.putInBody("id", orderID);
        PROCESSED_REQUEST.request();
    }

    public static void print(long orderID) {
        PrinterManager.print(OrderPrinterPageFactory.getFor(orderID));
        OrderUpdatesListener.setColor(orderID, OrderUpdatesListener.Colors.LOOKED);
    }

}
