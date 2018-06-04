package gew.photo.camera;

public enum ImageFormat
{
    GIF("GIF"),
    PNG("PNG"),
    JPG("JPG"),
    BMP("BMP"),
    WBMP("WBMP");

    private final String format;
    ImageFormat(String f) {
        format = f;
    }
    public String value()
    {
        return this.format;
    }

}
