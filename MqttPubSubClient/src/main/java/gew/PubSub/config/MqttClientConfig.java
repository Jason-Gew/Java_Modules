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
    private Integer pubQos;
    private Integer subQos;
    private List<String> autoPubTopics;
    private List<String> autoSubTopics;
    private Boolean enableOutQueue;

    public String getBroker() { return broker; }
    public void setBroker(String broker) { this.broker = broker; }

    public Integer getKeepAlive() { return keepAlive; }
    public void setKeepAlive(Integer keepAlive) { this.keepAlive = keepAlive; }

    public Boolean getCleanSession() { return cleanSession; }
    public void setCleanSession(Boolean cleanSession) { this.cleanSession = cleanSession; }

    public String getClientID() { return clientID; }
    public void setClientID(String clientID) { this.clientID = clientID; }

    public Boolean getAutoReconnect() { return autoReconnect; }
    public void setAutoReconnect(final Boolean autoReconnect) { this.autoReconnect = autoReconnect; }

    public Boolean getEnableLogin() { return enableLogin; }
    public void setEnableLogin(Boolean enableLogin) { this.enableLogin = enableLogin; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getPubQos() { return pubQos; }
    public void setPubQos(Integer pubQos) { this.pubQos = pubQos; }

    public Integer getSubQos() { return subQos; }
    public void setSubQos(Integer subQos) { this.subQos = subQos; }

    public List<String> getAutoPubTopics() { return autoPubTopics; }
    public void setAutoPubTopics(List<String> autoPubTopics) { this.autoPubTopics = autoPubTopics; }

    public List<String> getAutoSubTopics() { return autoSubTopics; }
    public void setAutoSubTopics(List<String> autoSubTopics) { this.autoSubTopics = autoSubTopics; }

    public Boolean getEnableOutQueue() { return enableOutQueue; }
    public void setEnableOutQueue(Boolean enableOutQueue) { this.enableOutQueue = enableOutQueue; }

}
