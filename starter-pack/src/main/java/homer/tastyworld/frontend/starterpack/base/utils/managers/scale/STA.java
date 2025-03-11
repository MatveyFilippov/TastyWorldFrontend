package homer.tastyworld.frontend.starterpack.base.utils.managers.scale;

public enum STA {  // Scale status

    STABLE ('S'),
    UNSTABLE ('U'),
    ERROR ('F'),
    UNKNOWN ('?');

    private final char STATUS_CHAR;

    STA (char statusChar) {
        STATUS_CHAR = statusChar;
    }

    public static STA parse(byte[] packet) {
        char staChar = (char) packet[2];
        for (STA sta : values()) {
            if (sta.STATUS_CHAR == staChar) {
                return sta;
            }
        }
        return UNKNOWN;
    }

}
