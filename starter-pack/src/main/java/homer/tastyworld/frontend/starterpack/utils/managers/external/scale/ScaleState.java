package homer.tastyworld.frontend.starterpack.utils.managers.external.scale;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    private static int getPacketStartIndex(byte[] buffer) {
        for (int i = 0; i < buffer.length - 1; i++) {
            if (buffer[i] == SOH && buffer[i + 1] == STX) {
                return i;
            }
        }
        return -1;
    }

    static Optional<ScaleState> createFromPacket(byte[] packet) {
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

    static boolean predicateBuffer(Predicate<ScaleState> predicate, byte[] buffer) {
        do {
            int startIndex = getPacketStartIndex(buffer);
            if (startIndex == -1 || buffer.length < startIndex + 12) {  // 12 byte packet (SOH+STX + 10 bytes of data)
                return false;
            }
            int lastIndex = startIndex + 12;

            byte[] packet = Arrays.copyOfRange(buffer, startIndex, lastIndex);
            Optional<ScaleState> state = createFromPacket(packet);

            if (state.isPresent() && predicate.test(state.get())) {
                return true;
            }

            buffer = Arrays.copyOfRange(buffer, lastIndex, buffer.length);
        } while (true);
    }

    static List<ScaleState> createFromBuffer(byte[] buffer) {
        List<ScaleState> states = new ArrayList<>();
        predicateBuffer(states::add, buffer);
        return states;
    }

}
