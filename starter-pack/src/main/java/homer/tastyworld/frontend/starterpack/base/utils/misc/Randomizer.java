package homer.tastyworld.frontend.starterpack.base.utils.misc;

import java.util.Random;

public class Randomizer {

    private static final Random RANDOM = new Random();
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
    public static final int CHARACTERS_LENGTH = CHARACTERS.length();

    public static String getString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS_LENGTH);
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
