package gew.photo.camera;

public enum ImageNaming {

    STATIC("static"),
    INCREMENT("increment"),
    TIMESTAMP("timestamp");

    private final String value;
    ImageNaming(String s) {
        value = s;
    }
    public String value() {
        return this.value;
    }
}
