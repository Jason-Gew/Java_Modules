package gew.PubSub.mqtt;

public interface DefaultClient
{
    boolean isConnected();
    Boolean initialze();
    boolean connect() throws Exception;
    void disconnect();
}
