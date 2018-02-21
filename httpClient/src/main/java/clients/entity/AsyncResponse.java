package clients.entity;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Jason/GeW
 */
public class AsyncResponse implements Callback
{

    public AsyncResponse() { }

    @Override
    public void completed(HttpResponse httpResponse) {
        UnifiedResponse response = new UnifiedResponse(httpResponse.getStatus(),
                httpResponse.getStatusText(), httpResponse.getBody());
        System.out.println(response);
    }

    @Override
    public void failed(UnirestException e) {
        System.err.println("Async Operation Failed: " + e.getMessage());
    }

    @Override
    public void cancelled() {
        System.err.println("Async Operation Cancelled.");
    }
}
