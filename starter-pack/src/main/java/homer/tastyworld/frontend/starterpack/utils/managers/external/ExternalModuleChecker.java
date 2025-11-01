package homer.tastyworld.frontend.starterpack.utils.managers.external;

import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterChecker;
import homer.tastyworld.frontend.starterpack.utils.managers.external.scale.ScaleChecker;
import homer.tastyworld.frontend.starterpack.utils.managers.external.sound.SoundChecker;

public abstract class ExternalModuleChecker {

    public abstract void check();

    public static void checkAll() {
        new SoundChecker().check();
        new PrinterChecker().check();
        new ScaleChecker().check();
    }

}
