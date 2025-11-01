package homer.tastyworld.frontend.pos.creator.core.vkb;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

class PromptsProcessor {

    private record StringPair(String first, String second) {}
    private record StringDistance(String string, int distance) {}

    private static final AppLogger logger = AppLogger.getFor(PromptsProcessor.class);
    private static final String[] EMPTY_RESULT = new String[0];
    private static final CacheableFunction<StringPair, Integer> cacheableGetLevenshteinDistance = CacheManager.getForFunction(PromptsProcessor::getLevenshteinDistance);
    private final Set<String> usedPromptsSet;
    private final File usedPromptsFile;

    public PromptsProcessor(File usedPrompts) {
        Set<String> tempPrompts;
        try (FileInputStream fileIn = new FileInputStream(usedPrompts); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            tempPrompts = (Set<String>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            tempPrompts = new HashSet<>();
        }
        this.usedPromptsSet = tempPrompts;
        this.usedPromptsFile = usedPrompts;
    }

    private static int getLevenshteinDistance(StringPair pair) {
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
        if (input == null || input.isBlank() || qty < 1) {
            return EMPTY_RESULT;
        }

        PriorityQueue<StringDistance> heap = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.distance, a.distance)
        );
        for (String string : usedPromptsSet) {
            int distance = cacheableGetLevenshteinDistance.applyWithCache(new StringPair(input, string));

            StringDistance stringDistance = new StringDistance(string, distance);

            if (heap.size() < qty) {
                heap.offer(stringDistance);
            } else if (distance < heap.peek().distance) {
                heap.poll();
                heap.offer(stringDistance);
            }
        }

        String[] result = new String[heap.size()];
        for (int i = heap.size() - 1; i >= 0; i--) {
            result[i] = heap.poll().string;
        }
        return result;
    }

    public void put(String input) {
        usedPromptsSet.add(input);
    }

    public void save() {
        try (FileOutputStream fileOut = new FileOutputStream(usedPromptsFile); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(usedPromptsSet);
        } catch (Exception ex) {
            logger.error("Can't save VirtualKeyboard used prompts to file", ex);
        }
    }

}
