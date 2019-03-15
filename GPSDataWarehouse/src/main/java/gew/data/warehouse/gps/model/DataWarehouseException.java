package gew.data.warehouse.gps.model;

/**
 * @author Jason/GeW
 * @since 2019-03-06
 */
public class DataWarehouseException extends RuntimeException {

    public DataWarehouseException(String message) {
        super(message);
    }

    public DataWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}
