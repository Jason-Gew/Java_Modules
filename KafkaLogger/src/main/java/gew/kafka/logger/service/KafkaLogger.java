package gew.kafka.logger.service;

import gew.kafka.logger.entity.LogMessage;
import gew.kafka.logger.entity.LogLevel;

/**
 * @author Jason/Ge Wu
 */
public interface KafkaLogger
{

    static KafkaLogger getInstance(){
        return null;
    }

    static void setRootLevel(LogLevel level) {
        //TODO
     }

    String timestamp();
    void getSystemInfo();

    void debug(final String classFile, final String methodName, final String message);
    void debug(final String topic, final String classFile, final String methodName, final String message);

    void info(final String classFile, final String methodName, final String message);
    void info(final String topic, final String classFile, final String methodName, final String message);

    void warn(final String classFile, final String methodName, final String message);
    void warn(final String topic, final String classFile, final String methodName, final String message);

    void error(final String classFile, final String methodName, final String message);
    void error(final String topic, final String classFile, final String methodName, final String message);

    void fatal(final String classFile, final String methodName, final String message);
    void fatal(final String topic, final String classFile, final String methodName, final String message);

    void logging(LogMessage logMessage);
    void logging(String topic, LogMessage logMessage);

}
