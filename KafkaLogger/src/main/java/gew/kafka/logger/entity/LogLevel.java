package gew.kafka.logger.entity;

/**
 * @author Jason/Ge Wu
 */
public enum LogLevel
{
    DEBUG("Debug"),
    INFO("Info"),
    WARN("Warn"),
    ERROR("Error"),
    FATAL("Fatal"),
    OFF("Off");

    private String level;

    LogLevel(String level) {
        this.level = level;
    }

    public String getValue() {
        return level;
    }
}
