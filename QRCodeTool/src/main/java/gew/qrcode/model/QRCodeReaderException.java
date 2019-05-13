package gew.qrcode.model;

/**
 * @author Jason/GeW
 * @since 2019-01-01
 */
public class QRCodeReaderException extends Exception {

    public QRCodeReaderException(String message) {
        super(message);
    }

    public QRCodeReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
