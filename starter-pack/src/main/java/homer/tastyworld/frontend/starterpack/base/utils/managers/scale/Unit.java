package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

import java.util.Arrays;

public enum Unit {

    KG,
    UNKNOWN;

    public static Unit parse(byte[] packet) {
        String unitStr = new String(Arrays.copyOfRange(packet, 10, 12)).toLowerCase();
        for (Unit unit : values()) {
            if (unitStr.equals(unit.name().toLowerCase())) {
                return unit;
            }
        }
        return UNKNOWN;
    }

}
