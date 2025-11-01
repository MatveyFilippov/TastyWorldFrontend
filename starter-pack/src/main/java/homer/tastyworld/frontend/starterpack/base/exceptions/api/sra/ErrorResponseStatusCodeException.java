package homer.tastyworld.frontend.starterpack.base.exceptions.api.sra;

import homer.tastyworld.frontend.starterpack.api.sra.ErrorResponseBody;
import homer.tastyworld.frontend.starterpack.base.exceptions.api.engine.UnexpectedResponseStatusCodeException;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class ErrorResponseStatusCodeException extends UnexpectedResponseStatusCodeException {

    public final ErrorResponseBody errorResponseBody;

    public ErrorResponseStatusCodeException(String message, ClassicHttpResponse response, int expectedStatusCode, ErrorResponseBody errorResponseBody) {
        super(message, response, expectedStatusCode);
        this.errorResponseBody = errorResponseBody;
    }

    public ErrorResponseStatusCodeException(ClassicHttpResponse response, int expectedStatusCode, ErrorResponseBody errorResponseBody) {
        this(
                "Faced with unexpected error response status code (Code: %s; Body: %s)".formatted(
                        response.getCode(), errorResponseBody
                ),
                response,
                expectedStatusCode, errorResponseBody
        );
    }

}
