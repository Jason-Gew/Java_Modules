package clients;

import clients.entity.UnifiedResponse;
import clients.unirest.UnirestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason/Ge Wu
 */
public class Main
{
    private static final String proxyHost = "103.85.24.48";     // Your Valid Proxy Host Address
    private static final Integer proxyPort = 6666;              // Your Valid Proxy Port Number

    private static final String GET_GLOBAL_IP = "https://api.ipify.org";
    private static final String GET_GLOBAL_IP_DETAILS = "https://ifconfig.co/json";

    private static final String HTTP_TEST_SERVER = "http://ptsv2.com/";

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

//        unirestClientExample1();

//        unirestClientExample2(true);

//        unirestClientExample3();

//        unirestPostExample();

        long endTime = System.currentTimeMillis();
        System.out.println("==> System Time Utilized: [" + (endTime - startTime) + "] ms...");
        System.exit(0);
    }

    private static void unirestClientExample1()
    {
        UnirestClient httpClient = new UnirestClient();
        UnifiedResponse response = httpClient.doGet(GET_GLOBAL_IP_DETAILS, true);
        ObjectMapper mapper = new ObjectMapper();

        if(response.getResultBody() instanceof JsonNode) {
            JsonNode result = (JsonNode) response.getResultBody();
            System.out.println(result.toString());
        } else {
            System.out.println(response);
        }
    }

    private static void unirestClientExample2(boolean enableProxy)
    {
        UnirestClient httpClient = new UnirestClient();
        if(enableProxy) {
            httpClient.setEnableProxy(true);
            httpClient.setProxyHost(proxyHost);
            httpClient.setProxyPort(proxyPort);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("format", "json");
        UnifiedResponse response = httpClient.doGet(GET_GLOBAL_IP, query);
        System.out.println(response);
    }

    private static void unirestClientExample3()
    {
        String testServerSuffix = "/t/8xh3g-1518301505/post";
        UnirestClient httpClient = new UnirestClient();
        Map<String, String> header = new HashMap<>();
        header.put("Header_Parameter", "header1");
        header.put("Content-Type", "application/json");
        UnifiedResponse response2 = httpClient.secureGet(HTTP_TEST_SERVER + testServerSuffix
                , "test", "Hello World".toCharArray(), header);

        System.out.println(response2);
    }

    private static void unirestPostExample()
    {
        String testServerSuffix = "/t/olvpo-1518305698/post";
        UnirestClient httpClient = new UnirestClient();
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        String body = "{\"Message\": \"Hello World\"}";
        UnifiedResponse response = httpClient.doPost(HTTP_TEST_SERVER + testServerSuffix,
                body,  header);

        System.out.println(response);
    }

}
