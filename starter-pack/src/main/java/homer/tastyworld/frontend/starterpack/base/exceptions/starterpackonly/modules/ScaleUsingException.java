package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.modules;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class ScaleUsingException extends DisplayedException {

    private static final AppLogger logger = AppLogger.getFor(ScaleUsingException.class);
    private final String toLog, toPrint;

    public ScaleUsingException(String toLog, String toPrint) {
        super(toLog);
        this.toLog = toLog;
        this.toPrint = toPrint;
    }

    @Override
    protected void action() {
        logger.errorOnlyServerNotify(toLog, null);
        AlertWindow.showError("Ошибка взвешивания", toPrint, true);
    }

}
