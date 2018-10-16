package gew.pubsub.config;

/**
 * @author Jason/GeW
 */
public class MQTTClientConfig {

    private String broker;
    private Integer keepAlive;
    private Boolean cleanSession;
    private String clientID;
    private Integer maxInFlight;
    private Boolean enableLogin;
    private String username;
    private String password;
    private Boolean enableSSL;
    private Integer pubQos;
    private Integer subQos;
    private Boolean enableOutQueue;

    public String getBroker() { return broker; }
    public void setBroker(final String broker) { this.broker = broker; }

    public Integer getKeepAlive() { return keepAlive; }
    public void setKeepAlive(final Integer keepAlive) { this.keepAlive = keepAlive; }

    public Boolean getCleanSession() { return cleanSession; }
    public void setCleanSession(final Boolean cleanSession) { this.cleanSession = cleanSession; }

    public String getClientID() { return clientID; }
    public void setClientID(final String clientID) { this.clientID = clientID; }

    public Integer getMaxInFlight() { return maxInFlight; }
    public void setMaxInFlight(Integer maxInFlight) { this.maxInFlight = maxInFlight; }

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

    public Boolean getEnableOutQueue() { return enableOutQueue; }
    public void setEnableOutQueue(final Boolean enableOutQueue) { this.enableOutQueue = enableOutQueue; }

}
