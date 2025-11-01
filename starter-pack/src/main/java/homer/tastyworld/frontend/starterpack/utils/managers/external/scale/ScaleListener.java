package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.function.Predicate;

class ScaleListener implements SerialPortDataListener {

    private final SerialPort comPort;
    private final Predicate<ScaleState> predicate;
    private volatile boolean isSleeping = true;
    private byte[] buffer = new byte[0];

    public ScaleListener(SerialPort comPort, Predicate<ScaleState> predicate) {
        this.comPort = comPort;
        this.predicate = predicate;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (isSleeping) {
            return;
        }

        byte[] newData = new byte[comPort.bytesAvailable()];
        comPort.readBytes(newData, newData.length);
        byte[] tempBuffer = new byte[buffer.length + newData.length];
        System.arraycopy(buffer, 0, tempBuffer, 0, buffer.length);
        System.arraycopy(newData, 0, tempBuffer, buffer.length, newData.length);
        buffer = tempBuffer;

        if (ScaleState.predicateBuffer(predicate, buffer)) {
            isSleeping = true;
        }
    }

    public void wakeUp() {
        isSleeping = false;
    }

}
