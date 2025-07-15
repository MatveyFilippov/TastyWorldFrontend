package homer.tastyworld.frontend.starterpack.order.core.names;

import java.util.Optional;

public class NumericalNameController extends NameController {

    public final int min;
    public final int max;
    private int lastUsed;

    public NumericalNameController(int min, int max) {
        this.min = min;
        this.max = max;
        lastUsed = max;
    }

    private int next() {
        lastUsed++;
        if (lastUsed > max) {
            lastUsed = min;
        }
        return lastUsed;
    }

    @Override
    public void back() {
        lastUsed--;
        if (lastUsed < min) {
            lastUsed = max;
        }
    }

    @Override
    protected Optional<String> tryToFindFreeName() {
        for (int i = min; i <= max; i++) {
            String result = String.valueOf(next());
            if (isFree(result)) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

}
