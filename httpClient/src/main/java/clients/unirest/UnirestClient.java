package clients.unirest;

import clients.entity.AsyncResponse;
import clients.entity.UnifiedResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;


/**
 * Sample methods for using Unirest Http Client...
 * @author Jason/Ge Wu
 */
public class UnirestClient
{

    private boolean enableAsync;
    private boolean enableProxy;
    private boolean enableBasicAuth;
    private String proxyHost;
    private Integer proxyPort;


    private static final String PROTOCOL_PREFIX = "http";

    public UnirestClient() {  }

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


    public void close()
    {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            System.err.println("Unirest Client Close Failed: " + e.getMessage());
        }
    }


    public UnifiedResponse doGet(final String url, Boolean jsonResponse)
    {
        UnifiedResponse response = null;

        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {

            try {
                if(enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                if(jsonResponse != null && jsonResponse) {
                    HttpResponse<JsonNode> httpResponse = Unirest.get(url).asJson();
                    response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());

                } else {
                    HttpResponse<String> httpResponse = Unirest.get(url).asString();
                    response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());
                }
//                System.out.println("Unirest HTTP GET Status: " + httpResponse.getStatus() + " " + httpResponse.getStatusText());


            } catch (UnirestException e) {
                System.err.println("Unirest HTTP GET Method Error: " + e.getMessage());
            }
        } else {
            System.err.println("=> Invalid URL");
        }
        return response;
    }

/*    @SuppressWarnings(value={"unchecked"})
    public Future<HttpResponse<String>> asyncGet(final String url, Map<String, String> header)
    {
        Future<HttpResponse<String>> responseFuture = null;

        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {
            if(header == null) {
                throw new IllegalArgumentException("Header Parameters Map Cannot be Null");
            }
            header.keySet().removeIf(key -> key == null || key.isEmpty());       // Remove Invalid Key - value
            header.values().removeIf(value -> value == null || value.isEmpty()); // Remove Invalid Key - value
            try{
                if (enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                responseFuture = Unirest.get(url).headers(header).asStringAsync(new AsyncResponse());
            } catch (Exception err) {
                System.err.println("Async GET Method Exception: " + err.getMessage());
            }

        } else {
            System.err.println("Invalid URL");
        }
        return responseFuture;
    }*/

    public UnifiedResponse doGet(final String url, Map<String, Object> queryPara)
    {
        return  doGet(url, queryPara, new HashMap<>());
    }

    public UnifiedResponse doGet(final String url, Map<String, Object> queryPara, Map<String, String> header)
    {
        UnifiedResponse response = null;

        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {
            HttpResponse<String> httpResponse;

            if(queryPara == null) {
                throw new IllegalArgumentException("Query Parameters Map Cannot be Null");
            }
            queryPara.keySet().removeIf(key -> key == null || key.isEmpty());    // Remove Invalid Key - value
            queryPara.values().removeIf(Objects::isNull);                        // Remove Invalid Key - value

            if(header == null) {
                throw new IllegalArgumentException("Header Parameters Map Cannot be Null");
            }
            header.keySet().removeIf(key -> key == null || key.isEmpty());       // Remove Invalid Key - value
            header.values().removeIf(value -> value == null || value.isEmpty()); // Remove Invalid Key - value

            try {
                if (enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                httpResponse = Unirest.get(url)
                        .queryString(queryPara)
                        .headers(header)
                        .asString();        // Modify return body format if necessary...
                response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());

            } catch (UnirestException e) {
                System.err.println("Unirest HTTP GET Method Error: " + e.getMessage());
            }

        } else {
            System.err.println("Invalid URL");
        }
        return response;
    }



    public UnifiedResponse secureGet(final String url, final String username, char[] password)
    {
        return secureGet(url, username, password, new HashMap<>());
    }

    public UnifiedResponse secureGet(final String url, final String username, char[] password, Map<String, String> header)
    {
        UnifiedResponse response = null;

        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {

            HttpResponse<String> httpResponse;
            if(username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Invalid Basic Auth Username");
            } else if (password == null || password.length == 0) {
                throw new IllegalArgumentException("Invalid Basic Auth Password");
            } else if(header == null) {
                throw new IllegalArgumentException("Header Parameters Map Cannot be Null");
            } else {
                header.keySet().removeIf(key -> key == null || key.isEmpty());       // Remove Invalid Key - value
                header.values().removeIf(value -> value == null || value.isEmpty()); // Remove Invalid Key - value
            }

            try {
                if(enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                httpResponse = Unirest.get(url)
                        .headers(header)
                        .basicAuth(username, String.valueOf(password))
                        .asString();        // Modify return body format if necessary...
                response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());
                System.out.println("Unirest HTTP GET Status: " + httpResponse.getStatus() + " " + httpResponse.getStatusText());

            } catch (UnirestException e) {
                System.err.println("Unirest HTTP GET Method Error: " + e.getMessage());
            }
        } else {
            System.err.println("=> Invalid URL");
        }
        return response;
    }

    public UnifiedResponse doPost(final String url, final String data)
    {
        return doPost(url, data, new HashMap<>());
    }

    public UnifiedResponse doPost(final String url, final String data, Map<String, String> header)
    {
        UnifiedResponse response = null;
        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {
            if(data == null) {
                throw new IllegalArgumentException("Invalid Body Data for HTTP POST");
            } else if (header == null) {
                throw new IllegalArgumentException("Header Parameters Map Cannot be Null");
            }
            HttpResponse<String> httpResponse;
            header.keySet().removeIf(key -> key == null || key.isEmpty());       // Remove Invalid Key - value
            header.values().removeIf(value -> value == null || value.isEmpty()); // Remove Invalid Key - value
            try {
                if(enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                httpResponse = Unirest.post(url)
                        .headers(header)
                        .body(data)
                        .asString();        // Modify return body format if necessary...
                response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());

            } catch (UnirestException e) {
                System.err.println("Unirest HTTP POST Method Error: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid URL");
        }
        return response;
    }

    public UnifiedResponse securePost(final String url, final String username, char[] password, final String data)
    {
        return securePost(url, username, password, data, new HashMap<>());
    }

    public UnifiedResponse securePost(final String url, final String username, char[] password, final String data, Map<String, String> header)
    {
        UnifiedResponse response = null;
        if(url != null && !url.isEmpty() && url.contains(PROTOCOL_PREFIX)) {
            if(data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Invalid Body Data for HTTP POST");
            } else if(username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Invalid Basic Auth Username");
            } else if (password == null || password.length == 0) {
                throw new IllegalArgumentException("Invalid Basic Auth Password");
            } else if(header == null) {
                throw new IllegalArgumentException("Header Parameters Map Cannot be Null");
            }
            header.keySet().removeIf(key -> key == null || key.isEmpty());       // Remove Invalid Key - value
            header.values().removeIf(value -> value == null || value.isEmpty()); // Remove Invalid Key - value
            HttpResponse<String> httpResponse;
            try {
                if(enableProxy && proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                    Unirest.setProxy(new HttpHost(proxyHost, proxyPort));
                } else {
                    Unirest.setProxy(null);
                }
                httpResponse = Unirest.post(url)
                        .headers(header)
                        .basicAuth(username, String.valueOf(password))
                        .body(data)
                        .asString();        // Modify return body format if necessary...
                response = new UnifiedResponse(httpResponse.getStatus(), httpResponse.getStatusText(), httpResponse.getBody());

            } catch (UnirestException e) {
                System.err.println("Unirest HTTP POST Method Error: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid URL");
        }
        return response;
    }

}
