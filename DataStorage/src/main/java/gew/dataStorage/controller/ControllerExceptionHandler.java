package gew.dataStorage.controller;


import gew.dataStorage.entity.RESTResponse;
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

/**
 * For unified error and exception handling with RESTResponse format...
 * @author Jason/GeW
 */
@ControllerAdvice
public class ControllerExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RESTResponse> argumentErrorHandler(MethodArgumentNotValidException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE, err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RESTResponse> requestParameterErrorHandler(MissingServletRequestParameterException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE, err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<RESTResponse> missingRequestPartHandler(MissingServletRequestPartException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<RESTResponse> unsatisfiedRequestParameterHandler(UnsatisfiedServletRequestParameterException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE, err.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RESTResponse>  methodNotSupportedErrorHandler(HttpRequestMethodNotSupportedException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE,
                err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RESTResponse> requestMessageUnreadableHandler(HttpMessageNotReadableException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.UNKNOWN, err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RESTResponse> mediaTypeNotSupportedErrorHandler(HttpMediaTypeNotSupportedException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE,
                err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RESTResponse> noHandlerFoundErrorHandler(NoHandlerFoundException err)
    {
        RESTResponse response = new RESTResponse(RESTResponse.FAILURE,
                err.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}