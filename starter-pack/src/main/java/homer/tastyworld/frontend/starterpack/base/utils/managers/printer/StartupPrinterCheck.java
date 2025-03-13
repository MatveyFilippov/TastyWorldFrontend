package homer.tastyworld.frontend.starterpack.base.utils.managers.printer;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class StartupPrinterCheck {

    public static void check() {
        String printerName = AppConfig.getPrinterName();
        if (printerName == null) {
            return;
        }
        if (!PrinterManager.isPrinterAvailable()) {
            PrinterManager.setDefaultPrinter();
            if (!PrinterManager.isPrinterAvailable()) {
                AlertWindow.showError(
                        "Ошибка с принтером", "Не удалось подключиться к принтеру, чеки не будут печаться", false
                );
                return;
            } else {
                AlertWindow.showInfo(
                        "Не удалось подключиться к принтеру с именем '" + printerName + "'",
                        "Сейчас используется принтер по умолчанию с именем '" + PrinterManager.getPrinterName() + "'",
                        false
                );
            }
        }
        PrinterManager.print(new PrinterPageFactory() {
            @Override
            protected void setContent() throws Exception {
                addLineLeft(AppDateTime.backendToLocal(AppDateTime.getBackendNowDateTime()).format(AppDateTime.DATETIME_FORMAT));
                addLineRight("Проверка принтера");
                addEmptyLines(1);
                addFullLine("(", '-', ")");
                output.write(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
                addLineCenter("ГОТОВ К РАБОТЕ");
                output.write(new byte[] {0x1B, 0x21, 0x00});  // Clean styles
                addDivider('*');
                addEmptyLines(4);
            }
        });
    }

}
