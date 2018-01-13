package gew.server.websocket.entity;


public class ClientInfo
{
    private String Username;
    private String SessionID;
    private String ConnectTime;

    public ClientInfo() { }

    public ClientInfo(String username, String sessionID, String connectTime) {
        Username = username;
        SessionID = sessionID;
        ConnectTime = connectTime;
    }

    public String getUsername() { return Username; }
    public void setUsername(final String username) { Username = username; }

    public String getSessionID() { return SessionID; }
    public void setSessionID(final String sessionID) { SessionID = sessionID; }

    public String getConnectTime() { return ConnectTime; }
    public void setConnectTime(final String connectTime) { ConnectTime = connectTime; }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "Username='" + Username + '\'' +
                ", SessionID='" + SessionID + '\'' +
                ", connectTime=" + ConnectTime +
                '}';
    }
}
