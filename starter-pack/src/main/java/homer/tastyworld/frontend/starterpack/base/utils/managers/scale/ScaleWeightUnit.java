package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

import java.util.Arrays;

public enum ScaleWeightUnit {

    KG,
    UNKNOWN;

    public static ScaleWeightUnit parse(byte[] packet) {
        String unitStr = new String(Arrays.copyOfRange(packet, 10, 12)).toLowerCase();
        for (ScaleWeightUnit unit : values()) {
            if (unitStr.equals(unit.name().toLowerCase())) {
                return unit;
            }
        }
        return UNKNOWN;
    }

}
