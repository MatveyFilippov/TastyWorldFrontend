package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

class ScaleListener implements SerialPortDataListener {

    private final SerialPort comPort;
    private final Consumer<ScaleState> consumer;
    private volatile boolean isSleeping = true;
    private final ByteBuffer buffer = ByteBuffer.allocate(4096);

    public ScaleListener(SerialPort comPort, Consumer<ScaleState> consumer) {
        this.comPort = comPort;
        this.consumer = consumer;
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
        buffer.put(newData);

        for (ScaleState state : ScaleState.popFromBuffer(buffer)) {
            consumer.accept(state);
        }
    }

    public void goSleep() {
        isSleeping = true;
    }

    public void wakeUp() {
        isSleeping = false;
    }

}
