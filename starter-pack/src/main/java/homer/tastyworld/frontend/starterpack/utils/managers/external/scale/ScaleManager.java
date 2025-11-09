package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import com.fazecast.jSerialComm.SerialPort;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ScaleManager implements AutoCloseable {

    private static boolean isInstanceAlreadyCreated = false;
    private static final AppLogger logger = AppLogger.getFor(ScaleManager.class);
    private static final SerialPort comPort;
    private final ScaleListener portListener;

    static {
        String comPortDescriptor = AppConfig.getScaleComPort();
        SerialPort comPortTemp = null;
        if (comPortDescriptor != null) {
            try {
                comPortTemp = SerialPort.getCommPort(comPortDescriptor);
                comPortTemp.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            } catch (Exception ex) {
                logger.error("Can't open COM port for scale", ex);
                comPortTemp = null;
            }
        }
        comPort = comPortTemp;
    }

    public ScaleManager(Consumer<ScaleState> consumer) throws ExternalModuleUnavailableException {
        if (isInstanceAlreadyCreated) {
            logger.error("Try to use scale, but it is already in use (exists not closed instance)", null);
            AlertWindows.showError("Ошибка взвешивания", "Весы недоступены, обратитесь за помощью к разарботчикам", false);
            throw new ExternalModuleUnavailableException("Try to use scale, but it is already in use (exists not closed instance)");
        }
        if (comPort == null || !comPort.openPort()) {
            if (AppConfig.getScaleComPort() != null) {
                logger.error("Try to use scale, but it is unavailable (or can't open port)", null);
                AlertWindows.showError("Ошибка взвешивания", "Весы недоступены, обратитесь за помощью к разарботчикам", false);
            }
            throw new ExternalModuleUnavailableException("Try to use scale, but it is unavailable (or can't open port)");
        }
        portListener = new ScaleListener(comPort, consumer);
        comPort.addDataListener(portListener);
        isInstanceAlreadyCreated = true;
    }

    public void pause() {
        portListener.goSleep();
    }

    public void proceed() {
        portListener.wakeUp();
    }

    @Override
    public void close() {
        portListener.goSleep();
        if (comPort != null) {
            comPort.removeDataListener();
            comPort.closePort();
        }
        isInstanceAlreadyCreated = false;
    }

}
