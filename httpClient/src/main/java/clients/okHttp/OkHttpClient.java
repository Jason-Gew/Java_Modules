package clients.okHttp;


/**
 * @author Jason/GeW
 */
public class OkHttpClient
{
    private boolean enableAsync;
    private boolean enableProxy;
    private boolean enableBasicAuth;
    private String proxyHost;
    private Integer proxyPort;

    public boolean isEnableAsync() { return enableAsync; }
    public void setEnableAsync(final boolean enableAsync) { this.enableAsync = enableAsync; }

    public boolean isEnableBasicAuth() { return enableBasicAuth; }
    public void setEnableBasicAuth(boolean enableBasicAuth) { this.enableBasicAuth = enableBasicAuth; }

    public boolean getEnableProxy() { return enableProxy; }
    public void setEnableProxy(boolean enableProxy) { this.enableProxy = enableProxy; }

    public String getProxyHost() { return proxyHost; }
    public void setProxyHost(final String proxyHost) { this.proxyHost = proxyHost; }

    public Integer getProxyPort() { return proxyPort; }
    public void setProxyPort(final Integer proxyPort) { this.proxyPort = proxyPort; }

    public OkHttpClient() { }


}
