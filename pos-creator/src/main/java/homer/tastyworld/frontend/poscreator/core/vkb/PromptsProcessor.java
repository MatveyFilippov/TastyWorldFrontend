package homer.tastyworld.frontend.poscreator.core.vkb;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class PromptsProcessor {

    static class StringPair {

        public final String first;
        public final String second;

        public StringPair(String first, String second) {
            this.first = first.intern();
            this.second = second.intern();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringPair that = (StringPair) o;
            return this.first == that.first && this.second == that.second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

    }

    private final Set<String> prompts;
    private final String path;
    private static final Map<StringPair, Integer> levenshteinCache = new HashMap<>();

    public PromptsProcessor(String path) {
        Set<String> tempPrompts;
        try (FileInputStream fileIn = new FileInputStream(path); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            tempPrompts = (Set<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            tempPrompts = new HashSet<>();
        }
        prompts = tempPrompts;
        this.path = path;
    }

    public String[] get(String input, int qty) {
        List<Map.Entry<String, Integer>> similarityList = new ArrayList<>();
        for (String str : prompts) {
            int distance = levenshteinDistance(new StringPair(input, str));
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
        prompts.add(input);
    }

    public void save() {
        try (FileOutputStream fileOut = new FileOutputStream(path); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(prompts);
        } catch (IOException ex) {
            AppLogger.GLOBAL_LOGGER.errorOnlyServerNotify("Can't save VirtualKeyboard prompts to file", ex);
        }
    }

    private static int levenshteinDistance(StringPair pair) {
        return levenshteinCache.computeIfAbsent(pair, strings -> {
            int[][] dp = new int[strings.first.length() + 1][strings.second.length() + 1];
            for (int i = 0; i <= strings.first.length(); i++) {
                for (int j = 0; j <= strings.second.length(); j++) {
                    if (i == 0) {
                        dp[i][j] = j;
                    } else if (j == 0) {
                        dp[i][j] = i;
                    } else {
                        dp[i][j] = Arrays.stream(new int[] {
                                dp[i - 1][j - 1] + (strings.first.charAt(i - 1) == strings.second.charAt(j - 1) ? 0 : 1),
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        }).min().orElse(Integer.MAX_VALUE);
                    }
                }
            }
            return dp[strings.first.length()][strings.second.length()];
        });
    }

}
