package gew.webview.model;


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
    public String value()
    {
        return this.name;
    }
}