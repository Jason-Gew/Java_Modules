package caching.memcache.client;

/**
 * @author Jason/GeW
 */
public class MClientConfig {

    private String address;
    private Integer connectTimeout = 60000;
    private Integer idleTimeout = 5000;
    private Integer connectionPoolSize = 1;
    private Boolean enableHeatBeat = true;
    private Boolean healSession = true;
    private Integer healSessionInterval = 5000;
    private boolean enableSASL = false;
    private String username;
    private String password;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public Boolean getEnableHeatBeat() {
        return enableHeatBeat;
    }

    public void setEnableHeatBeat(Boolean enableHeatBeat) {
        this.enableHeatBeat = enableHeatBeat;
    }

    public Boolean getHealSession() {
        return healSession;
    }

    public void setHealSession(Boolean healSession) {
        this.healSession = healSession;
    }

    public Integer getHealSessionInterval() {
        return healSessionInterval;
    }

    public void setHealSessionInterval(Integer healSessionInterval) {
        this.healSessionInterval = healSessionInterval;
    }

    public boolean isEnableSASL() {
        return enableSASL;
    }

    public void setEnableSASL(boolean enableSASL) {
        this.enableSASL = enableSASL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "MClientConfig{" +
                "address='" + address + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", idleTimeout=" + idleTimeout +
                ", connectionPoolSize=" + connectionPoolSize +
                ", enableHeatBeat=" + enableHeatBeat +
                ", healSession=" + healSession +
                ", healSessionInterval=" + healSessionInterval +
                '}';
    }
}
