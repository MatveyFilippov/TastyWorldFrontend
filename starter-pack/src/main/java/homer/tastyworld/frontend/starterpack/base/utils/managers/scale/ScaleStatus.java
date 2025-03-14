package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

public enum ScaleStatus {  // Scale status

    STABLE ('S'),
    UNSTABLE ('U'),
    ERROR ('F'),
    UNKNOWN ('?');

    private final char STATUS_CHAR;

    ScaleStatus(char statusChar) {
        STATUS_CHAR = statusChar;
    }

    public static ScaleStatus parse(byte[] packet) {
        char staChar = (char) packet[2];
        for (ScaleStatus scaleStatus : values()) {
            if (scaleStatus.STATUS_CHAR == staChar) {
                return scaleStatus;
            }
        }
        return UNKNOWN;
    }

}
