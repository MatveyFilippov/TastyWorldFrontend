package homer.tastyworld.frontend.pos.creator.core.vkb;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PromptsProcessor {

    record StringPair(String first, String second) {}

    private static final String[] EMPTY_RESULT = new String[0];
    private static final AppLogger logger = AppLogger.getFor(PromptsProcessor.class);
    private static final CacheProcessor<StringPair, Integer> levenshteinCache = CacheManager.register(PromptsProcessor::levenshteinDistance);
    private final Set<String> usedPromptsSet;
    private final File usedPromptsFile;

    public PromptsProcessor(File usedPrompts) {
        Set<String> tempPrompts;
        try (FileInputStream fileIn = new FileInputStream(usedPrompts); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            tempPrompts = (Set<String>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            tempPrompts = new HashSet<>();
        }
        usedPromptsSet = tempPrompts;
        this.usedPromptsFile = usedPrompts;
    }

    private static int levenshteinDistance(StringPair pair) {
        int[][] dp = new int[pair.first.length() + 1][pair.second.length() + 1];
        for (int i = 0; i <= pair.first.length(); i++) {
            for (int j = 0; j <= pair.second.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Arrays.stream(new int[] {dp[i - 1][j - 1] + (
                            pair.first.charAt(i - 1) == pair.second.charAt(j - 1) ? 0 : 1
                    ), dp[i - 1][j] + 1, dp[i][j - 1] + 1}).min().orElse(Integer.MAX_VALUE);
                }
            }
        }
        return dp[pair.first.length()][pair.second.length()];
    }

    public String[] get(String input, int qty) {
        if (input == null || input.isBlank()) {
            return EMPTY_RESULT;
        }
        List<Map.Entry<String, Integer>> similarityList = new ArrayList<>();
        for (String str : usedPromptsSet) {
            int distance = levenshteinCache.get(new StringPair(input, str));
            similarityList.add(new AbstractMap.SimpleEntry<>(str, distance));
        }
        similarityList.sort(Map.Entry.comparingByValue());

        String[] result = new String[Math.min(qty, similarityList.size())];
        for (int i = 0; i < result.length; i++) {
            result[i] = similarityList.get(i).getKey();
        }
        return result;
    }

    public void put(String input) {
        usedPromptsSet.add(input);
    }

    public void save() {
        try (FileOutputStream fileOut = new FileOutputStream(usedPromptsFile); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(usedPromptsSet);
        } catch (IOException ex) {
            logger.errorOnlyServerNotify("Can't save VirtualKeyboard used prompts to file", ex);
        }
    }

}
