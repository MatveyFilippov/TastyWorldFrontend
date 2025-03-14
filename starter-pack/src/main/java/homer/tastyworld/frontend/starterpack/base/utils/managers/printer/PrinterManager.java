package homer.tastyworld.frontend.starterpack.base.utils.managers.printer;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.modules.PrinterUsingException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

public class PrinterManager {

    private static final AppLogger logger = AppLogger.getFor(PrinterManager.class);
    private static PrintService printer = null;

    static {
        setPrinter(AppConfig.getPrinterName());
    }

    protected static String getPrinterName() {
        return printer != null ? printer.getName() : null;
    }

    public static void print(PrinterPageFactory printerPageFactory) {
        if (printer == null) {
            if (AppConfig.getPrinterName() != null) {
                throw new PrinterUsingException(
                        "Try to use printer, but it is unavailable",
                        "Принтер недоступен, обратитесь за помощью к разарботчикам"
                );
            }
            return;
        }
        DocPrintJob job = printer.createPrintJob();
        Doc doc = new SimpleDoc(printerPageFactory.getPage(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        try {
            job.print(doc, null);
        } catch (PrintException ex) {
            logger.error("PrinterManager occurred with problem while try to print", ex);
            AlertWindow.showError("Ошибка печати", "Не смог распечатать чек", false);
        }
    }

    public static void setPrinter(String name) {
        for (PrintService service : PrintServiceLookup.lookupPrintServices(null, null)) {
            if (service.getName().equalsIgnoreCase(name)) {
                printer = service;
                return;
            }
        }
        printer = null;
    }

    public static void setDefaultPrinter() {
        printer = PrintServiceLookup.lookupDefaultPrintService();
    }

}
