package reviewHelpful;


import com.omartech.review.client.DataClients;

/**
 * Created by OmarTech on 15-6-8.
 */
public class ClientsFetcher {
    public static DataClients tfidfClient = new DataClients("127.0.0.1:8123,127.0.0.1:8123");
    public static DataClients commentObjectClient = new DataClients("127.0.0.1:8124,127.0.0.1:8124");
}
