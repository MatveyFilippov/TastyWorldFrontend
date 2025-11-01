package homer.tastyworld.frontend.starterpack.utils.managers.external.printer;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;
import java.time.format.DateTimeFormatter;

public class PrinterChecker extends ExternalModuleChecker {

    @Override
    public void check() {
        PrinterManager.print(new PrinterPageFactory() {
            @Override
            protected void setContent() throws Exception {
                addLineLeft(AppDateTime.serviceToLocal(AppDateTime.getServiceNowDateTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
