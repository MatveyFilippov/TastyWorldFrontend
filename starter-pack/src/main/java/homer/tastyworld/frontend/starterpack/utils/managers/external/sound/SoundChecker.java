package homer.tastyworld.frontend.starterpack.utils.managers.external.sound;

import homer.tastyworld.frontend.starterpack.utils.managers.external.ExternalModuleChecker;

public class SoundChecker extends ExternalModuleChecker {

    @Override
    public void check() {
        SoundManager.playAlert();
    }

}
