package gew.photo.entity;

/**
 * @author Jason/Ge Wu
 * @since 2017-06-06
 */
public enum Status
{
    SUCCESS("Success"),
    FAIL("Fail"),
    UNKNOWN("Unknown");

    private final String name;
    Status(String s) {
        name = s;
    }
    public String value()
    {
        return this.name;
    }
}
