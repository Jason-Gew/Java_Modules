package gew.webServices.storage.controller;

import gew.webServices.storage.entity.UnifiedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Customized REST HTTP Exception Response under a UnifiedResponse.
 * @author Jason/Ge Wu
 */
@ControllerAdvice
public class ControllerExceptionHandler
{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UnifiedResponse> methodArgumentErrorHandler(MethodArgumentNotValidException err)
    {
        UnifiedResponse response = new UnifiedResponse(400, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<UnifiedResponse> methodArgumentTypeErrorHandler(MethodArgumentTypeMismatchException err)
    {
        UnifiedResponse response = new UnifiedResponse(400, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<UnifiedResponse> missingRequestParameterHandler(MissingServletRequestParameterException err)
    {
        UnifiedResponse response = new UnifiedResponse(400, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<UnifiedResponse> missingRequestPartHandler(MissingServletRequestPartException err)
    {
        UnifiedResponse response = new UnifiedResponse(404, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<UnifiedResponse> messageNotReadableErrorHandler(HttpMessageNotReadableException err)
    {
        UnifiedResponse response = new UnifiedResponse(400, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<UnifiedResponse> methodNotSupportErrorHandler(HttpRequestMethodNotSupportedException err)
    {
        UnifiedResponse response = new UnifiedResponse(405, UnifiedResponse.failure,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<UnifiedResponse> noHandlerFoundExceptionHandler(NoHandlerFoundException err)
    {
        UnifiedResponse response = new UnifiedResponse(520, UnifiedResponse.unknown,
                err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }
}
