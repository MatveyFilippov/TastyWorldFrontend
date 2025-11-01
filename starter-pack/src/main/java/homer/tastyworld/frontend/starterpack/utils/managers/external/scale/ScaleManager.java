package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import com.fazecast.jSerialComm.SerialPort;

public class ScaleManager implements AutoCloseable {

    private static final AppLogger logger = AppLogger.getFor(ScaleManager.class);
    private static final SerialPort comPort;
    private ScaleListener portListener = null;
    private volatile ScaleState state;

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

    public ScaleManager() throws ExternalModuleUnavailableException {
        if (comPort == null || !comPort.openPort()) {
            if (AppConfig.getScaleComPort() != null) {
                logger.error("Try to use scale, but it is unavailable (or can't open port)", null);
                AlertWindows.showError("Ошибка взвешивания", "Весы недоступены, обратитесь за помощью к разарботчикам", false);
            }
            throw new ExternalModuleUnavailableException("Try to use scale, but it is unavailable (or can't open port)");
        }
        if (portListener == null) {
            portListener = new ScaleListener(comPort, this::setScaleState);
        }
        comPort.addDataListener(portListener);
    }

    private synchronized boolean setScaleState(ScaleState state) {
        this.state = state;
        notifyAll();
        return true;
    }

    public synchronized ScaleState getScaleState() throws InterruptedException {
        ScaleState tempState = null;
        while (tempState == null) {
            portListener.wakeUp();
            wait();
            if (state != null) {
                tempState = state;
            }
        }
        return tempState;
    }

    @Override
    public void close() {
        if (comPort != null) {
            comPort.removeDataListener();
            comPort.closePort();
        }
    }

}
