package homer.tastyworld.frontend.starterpack.api.sra;

import org.jetbrains.annotations.Nullable;
import java.net.URI;
import java.util.Map;

public record ErrorResponseBody(
        URI type,
        String title,
        Integer status,
        String detail,
        URI instance,
        @Nullable Map<String, Object> properties
) {}
