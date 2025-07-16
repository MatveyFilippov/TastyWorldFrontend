package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

import java.util.Arrays;

public class ScaleState {

    public enum Status {

        STABLE ('S'),
        UNSTABLE ('U'),
        ERROR ('F'),
        UNKNOWN ('?');

        public final char STATUS_CHAR;

        Status(char statusChar) {
            STATUS_CHAR = statusChar;
        }

    }

    public enum Unit {

        KG,
        UNKNOWN;

    }

    static Status parseStatus(byte[] packet) {
        char staChar = (char) packet[2];
        for (Status status : Status.values()) {
            if (status.STATUS_CHAR == staChar) {
                return status;
            }
        }
        return Status.UNKNOWN;
    }

    static Unit parseUnit(byte[] packet) {
        String unitStr = new String(Arrays.copyOfRange(packet, 10, 12)).toLowerCase();
        for (Unit unit : Unit.values()) {
            if (unitStr.equalsIgnoreCase(unit.name())) {
                return unit;
            }
        }
        return Unit.UNKNOWN;
    }

    public final Status STATUS;
    public final double WEIGHT;
    public final Unit UNIT;

    ScaleState(Status status, double weight, Unit unit) {
        this.STATUS = status;
        this.WEIGHT = weight;
        this.UNIT = unit;
    }

    public ScaleState copy() {
        return new ScaleState(STATUS, WEIGHT, UNIT);
    }

}
