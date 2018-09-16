package gew.caching.entity;


/**
 * @author Jason/GeW
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
    public String toString()
    {
        return this.name;
    }
}