package gew.qrcode.model;

/**
 * @author Jason/GeW
 * @since 2019-01-01
 */
public class QRCodeWriterException extends Exception {

    public QRCodeWriterException(String message) {
        super(message);
    }

    public QRCodeWriterException(String message, Throwable cause) {
        super(message, cause);
    }
}
