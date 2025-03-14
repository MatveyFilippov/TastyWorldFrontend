package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.modules.ScaleUsingException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import java.util.Arrays;

public class ScaleManager implements AutoCloseable {

    public static class ScaleState {

        public final ScaleStatus STATUS;
        public final double WEIGHT;
        public final ScaleWeightUnit UNIT;

        ScaleState(ScaleStatus status, double weight, ScaleWeightUnit unit) {
            this.STATUS = status;
            this.WEIGHT = weight;
            this.UNIT = unit;
        }

    }

    @FunctionalInterface
    public interface WeightHandler { void handle(ScaleState scaleState); }

    private static final AppLogger logger = AppLogger.getFor(ScaleManager.class);
    private static final byte SOH = 0x01; // Start byte 1
    private static final byte STX = 0x02; // Start byte 2
    public static final boolean IS_SCALE_AVAILABLE;
    private static final SerialPort COM_PORT;
    private ScaleState scaleState;
    private WeightHandler weightHandler;
    private boolean isAsking = false;
    private byte[] buffer = new byte[0];

    static {
        String scaleComPort = AppConfig.getScaleComPort();
        if (scaleComPort != null) {
            COM_PORT = SerialPort.getCommPort(AppConfig.getScaleComPort());
            COM_PORT.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            IS_SCALE_AVAILABLE = COM_PORT.openPort();
            COM_PORT.closePort();
        } else {
            COM_PORT = null;
            IS_SCALE_AVAILABLE = false;
        }
    }

    public ScaleManager() {
        if (!IS_SCALE_AVAILABLE || !COM_PORT.openPort()) {
            throw new ScaleUsingException(
                    "Try to use scale, but it is unavailable (or can't open port)",
                    "Весы недоступны, обратитесь за помощью к разарботчикам"
            );
        }
        COM_PORT.addDataListener(new SerialPortDataListener() {

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (weightHandler == null && !isAsking) {
                    return;
                }
                byte[] newData = new byte[COM_PORT.bytesAvailable()];
                COM_PORT.readBytes(newData, newData.length);
                byte[] tempBuffer = new byte[buffer.length + newData.length];
                System.arraycopy(buffer, 0, tempBuffer, 0, buffer.length);
                System.arraycopy(newData, 0, tempBuffer, buffer.length, newData.length);
                buffer = tempBuffer;
                processBuffer();
                if (weightHandler != null && scaleState != null) {
                    if (Platform.isFxApplicationThread()) {
                        weightHandler.handle(scaleState);
                    } else {
                        Platform.runLater(() -> weightHandler.handle(scaleState));
                    }
                }
                scaleState = null;
            }

        });
    }

    private void processBuffer() {
        do {
            int startIndex = getPacketStartIndex();
            if (startIndex == -1 || buffer.length < startIndex + 12) {  // 12 byte packet (SOH+STX + 10 bytes of data)
                break;
            }
            byte[] packet = Arrays.copyOfRange(buffer, startIndex, startIndex + 12);
            processPacket(packet);
            buffer = Arrays.copyOfRange(buffer, startIndex + 12, buffer.length);
        } while (true);
    }

    private int getPacketStartIndex() {
        for (int i = 0; i < buffer.length - 1; i++) {
            if (buffer[i] == SOH && buffer[i + 1] == STX) {
                return i;
            }
        }
        return -1;
    }

    private void processPacket(byte[] packet) {
        if (packet[0] != SOH || packet[1] != STX) {  // Check only the starting bytes
            logger.errorOnlyServerNotify("ScaleManager occurred with invalid packet", null);
            return;
        }

        String weightStr = new String(Arrays.copyOfRange(packet, 4, 10));
        double weight;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException ex) {
            logger.errorOnlyServerNotify("ScaleManager can't parse weight", ex);
            return;
        }

        scaleState = new ScaleState(ScaleStatus.parse(packet), weight, ScaleWeightUnit.parse(packet));
    }

    public void setWeightHandler(WeightHandler weightHandler) {
        this.weightHandler = weightHandler;
    }

    public ScaleState getScaleState() throws InterruptedException {
        isAsking = true;
        ScaleState tempScaleState = null;
        while (tempScaleState == null) {
            Thread.sleep(100);
            tempScaleState = scaleState;
        }
        isAsking = false;
        return tempScaleState;
    }

    @Override
    public void close() {
        if (COM_PORT != null) {
            COM_PORT.removeDataListener();
            COM_PORT.closePort();
        }
    }

}