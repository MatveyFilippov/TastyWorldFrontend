package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ScaleState {

    public enum Status {

        STABLE ('S'),
        UNSTABLE ('U'),
        ERROR ('F'),
        UNKNOWN ('?');

        final char statusCar;

        Status(char statusChar) {
            this.statusCar = statusChar;
        }

        static Status valueOf(byte[] packet) {
            char statusChar = (char) packet[2];
            for (Status status : Status.values()) {
                if (status.statusCar == statusChar) {
                    return status;
                }
            }
            return Status.UNKNOWN;
        }

    }

    public enum Unit {

        KG,
        UNKNOWN;

        static Unit valueOf(byte[] packet) {
            String unitStr = new String(packet, 10, 2, StandardCharsets.US_ASCII);
            for (Unit unit : Unit.values()) {
                if (unitStr.equalsIgnoreCase(unit.name())) {
                    return unit;
                }
            }
            return Unit.UNKNOWN;
        }

    }

    private static final AppLogger logger = AppLogger.getFor(ScaleState.class);
    private static final byte SOH = 0x01; // Packet start byte 1
    private static final byte STX = 0x02; // Packet start byte 2

    public final Status STATUS;
    public final double WEIGHT;
    public final Unit UNIT;

    ScaleState(Status status, double weight, Unit unit) {
        this.STATUS = status;
        this.WEIGHT = weight;
        this.UNIT = unit;
    }

    private static int findPacketStart(ByteBuffer buffer) {
        int startPos = buffer.position();
        int bytesToCheck = buffer.remaining() - 1;
        for (int i = 0; i < bytesToCheck; i++) {
            if (buffer.get(startPos + i) == SOH && buffer.get(startPos + i + 1) == STX) {
                return i;
            }
        }
        return -1;
    }

    static Optional<ScaleState> getFromPacket(byte[] packet) {
        if (packet[0] != SOH || packet[1] != STX) {  // Check only the starting bytes
            logger.error("Faced with invalid packet for ScaleState", null);
            return Optional.empty();
        }

        double weight;
        try {
            String weightStr = new String(packet, 4, 6, StandardCharsets.US_ASCII);
            weight = Double.parseDouble(weightStr);
        } catch (Exception ex) {
            logger.error("Can't parse weight for ScaleState", ex);
            return Optional.empty();
        }

        return Optional.of(new ScaleState(
                Status.valueOf(packet),
                weight,
                Unit.KG
        ));
    }

    static List<ScaleState> popFromBuffer(ByteBuffer buffer) {
        List<ScaleState> states = new ArrayList<>();
        buffer.flip();

        while (true) {
            int startIndex = findPacketStart(buffer);
            if (startIndex == -1 || buffer.remaining() < startIndex + 12) {
                break;
            }

            buffer.position(buffer.position() + startIndex);

            byte[] packet = new byte[12];
            buffer.get(packet);

            getFromPacket(packet).ifPresent(states::add);
        }

        buffer.compact();
        return states;
    }

}
