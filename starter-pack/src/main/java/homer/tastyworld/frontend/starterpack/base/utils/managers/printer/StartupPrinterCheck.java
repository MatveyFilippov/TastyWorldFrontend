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
        if (PrinterManager.getPrinterName() == null) {
            PrinterManager.setDefaultPrinter();
            if (PrinterManager.getPrinterName() == null) {
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
        PrinterManager.print(new PrinterPageFactory(48) {
            @Override
            protected void setContent() throws Exception {
                addLineLeft(AppDateTime.backendToLocal(AppDateTime.getBackendNowDateTime()).format(AppDateTime.DATETIME_FORMAT));
                addLineRight("Проверка принтера");
                addEmptyLines(1);
                addFullLine("(", '-', ")");
                setFontStyle(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
                addLineCenter("ГОТОВ К РАБОТЕ");
                dropFontStyle();
                addDivider('*');
                addEmptyLines(4);
            }
        });
    }

}
