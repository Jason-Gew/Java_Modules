package gew.PubSub.config;


import java.util.List;

/**
 * @author Jason/GeW
 */
public class MqttClientConfig
{
    private String broker;
    private Integer keepAlive;
    private Boolean cleanSession;
    private String clientID;
    private Boolean autoReconnect;
    private Boolean enableLogin;
    private String username;
    private String password;
    private Boolean enableSSL;
    private Integer pubQos;
    private Integer subQos;
    private List<String> autoPubTopics;
    private List<String> autoSubTopics;
    private Boolean enableOutQueue;

    public String getBroker() { return broker; }
    public void setBroker(final String broker) { this.broker = broker; }

    public Integer getKeepAlive() { return keepAlive; }
    public void setKeepAlive(final Integer keepAlive) { this.keepAlive = keepAlive; }

    public Boolean getCleanSession() { return cleanSession; }
    public void setCleanSession(final Boolean cleanSession) { this.cleanSession = cleanSession; }

    public String getClientID() { return clientID; }
    public void setClientID(final String clientID) { this.clientID = clientID; }

    public Boolean getAutoReconnect() { return autoReconnect; }
    public void setAutoReconnect(final Boolean autoReconnect) { this.autoReconnect = autoReconnect; }

    public Boolean getEnableLogin() { return enableLogin; }
    public void setEnableLogin(final Boolean enableLogin) { this.enableLogin = enableLogin; }

    public String getUsername() { return username; }
    public void setUsername(final String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(final String password) { this.password = password; }

    public Boolean getEnableSSL() { return enableSSL; }
    public void setEnableSSL(final Boolean enableSSL) { this.enableSSL = enableSSL; }

    public Integer getPubQos() { return pubQos; }
    public void setPubQos(final Integer pubQos) { this.pubQos = pubQos; }

    public Integer getSubQos() { return subQos; }
    public void setSubQos(final Integer subQos) { this.subQos = subQos; }

    public List<String> getAutoPubTopics() { return autoPubTopics; }
    public void setAutoPubTopics(List<String> autoPubTopics) { this.autoPubTopics = autoPubTopics; }

    public List<String> getAutoSubTopics() { return autoSubTopics; }
    public void setAutoSubTopics(List<String> autoSubTopics) { this.autoSubTopics = autoSubTopics; }

    public Boolean getEnableOutQueue() { return enableOutQueue; }
    public void setEnableOutQueue(final Boolean enableOutQueue) { this.enableOutQueue = enableOutQueue; }

}
