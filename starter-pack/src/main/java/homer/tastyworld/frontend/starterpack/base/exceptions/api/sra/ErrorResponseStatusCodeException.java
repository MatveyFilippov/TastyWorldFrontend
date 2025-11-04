package homer.tastyworld.frontend.starterpack.base.exceptions.api.sra;

import homer.tastyworld.frontend.starterpack.api.sra.ErrorResponseBody;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class ErrorResponseStatusCodeException extends UnexpectedResponseStatusCodeException {

    public final ErrorResponseBody errorResponseBody;

    public ErrorResponseStatusCodeException(ClassicHttpResponse response, int expectedStatusCode, ErrorResponseBody errorResponseBody) {
        super(
                "Faced with unexpected response status code (Expected: %s; Actual: %s; Body: %s)".formatted(
                        expectedStatusCode, response.getCode(), errorResponseBody
                ),
                response.getCode(),
                expectedStatusCode
        );
        this.errorResponseBody = errorResponseBody;
    }

}
