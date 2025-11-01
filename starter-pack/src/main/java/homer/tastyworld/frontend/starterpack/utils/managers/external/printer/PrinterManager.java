package homer.tastyworld.frontend.starterpack.utils.managers.external.printer;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

public class PrinterManager {

    private static final AppLogger logger = AppLogger.getFor(PrinterManager.class);
    private static PrintService printer;

    static {
        String printerName = AppConfig.getPrinterName();
        if (printerName != null) {
            for (PrintService service : PrintServiceLookup.lookupPrintServices(null, null)) {
                if (service.getName().equalsIgnoreCase(printerName)) {
                    printer = service;
                    break;
                }
            }
        }
    }

    // public static String getPrinterName() {
    //     return printer != null ? printer.getName() : null;
    // }
    //
    // public static void setPrinter(String name) {
    //     for (PrintService service : PrintServiceLookup.lookupPrintServices(null, null)) {
    //         if (service.getName().equalsIgnoreCase(name)) {
    //             printer = service;
    //             return;
    //         }
    //     }
    //     printer = null;
    // }
    //
    // public static void setDefaultPrinter() {
    //     printer = PrintServiceLookup.lookupDefaultPrintService();
    // }

    public static void print(PrinterPageFactory printerPageFactory) {
        if (printer == null) {
            if (AppConfig.getPrinterName() != null) {
                logger.error("Try to use printer, but it is unavailable", null);
                AlertWindows.showError(
                        "Ошибка печати", "Принтер недоступен, обратитесь за помощью к разарботчикам", false
                );
            }
            return;
        }
        DocPrintJob job = printer.createPrintJob();
        Doc doc = new SimpleDoc(printerPageFactory.getPage(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        try {
            job.print(doc, null);
        } catch (Exception ex) {
            logger.error("PrinterManager occurred with problem while try to print", ex);
            AlertWindows.showError("Ошибка печати", "Не смог распечатать чек", false);
        }
    }

}
