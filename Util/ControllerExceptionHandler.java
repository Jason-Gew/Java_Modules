
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse> argumentErrorHandler(MethodArgumentNotValidException err) {
        RestResponse response = new RestResponse(400, Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestResponse> requestParameterErrorHandler(MissingServletRequestParameterException err) {
        RestResponse response = new RestResponse(400,  Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<RestResponse> missingRequestPartHandler(MissingServletRequestPartException err) {
        RestResponse response = new RestResponse(404, Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<RestResponse> unsatisfiedRequestParameterHandler(UnsatisfiedServletRequestParameterException err) {
        RestResponse response = new RestResponse(400,  Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestResponse>  methodNotSupportedErrorHandler(HttpRequestMethodNotSupportedException err) {
        RestResponse response = new RestResponse(405, Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse> requestMessageUnreadableHandler(HttpMessageNotReadableException err) {
        RestResponse response = new RestResponse(400, Status.FAIL,
                err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RestResponse> mediaTypeNotSupportedErrorHandler(HttpMediaTypeNotSupportedException err) {
        RestResponse response = new RestResponse(415, Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RestResponse> noHandlerFoundErrorHandler(NoHandlerFoundException err) {
        RestResponse response = new RestResponse(404, Status.FAIL, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
