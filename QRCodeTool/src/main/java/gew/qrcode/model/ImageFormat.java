package gew.qrcode.model;

/**
 * @author Jason/GeW
 * @since 2019-01-01
 */
public enum ImageFormat {

    JPG("jpg"),
    GIF("gif"),
    PNG("png"),
    BMP("bmp");

    private String format;

    ImageFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
